package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.AddressResponse;
import hr.abysalto.hiring.api.junior.dto.CreateAddressRequest;
import hr.abysalto.hiring.api.junior.dto.CreateBuyerRequest;
import hr.abysalto.hiring.api.junior.dto.CreateOrderItemRequest;
import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderItemResponse;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.dto.UpdateOrderStatusRequest;
import hr.abysalto.hiring.api.junior.model.Buyer;
import hr.abysalto.hiring.api.junior.model.BuyerAddress;
import hr.abysalto.hiring.api.junior.model.Order;
import hr.abysalto.hiring.api.junior.model.OrderItem;
import hr.abysalto.hiring.api.junior.model.OrderStatus;
import hr.abysalto.hiring.api.junior.repository.BuyerAddressRepository;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import hr.abysalto.hiring.api.junior.repository.OrderItemRepository;
import hr.abysalto.hiring.api.junior.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderManagerImpl implements OrderManager {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final BuyerRepository buyerRepository;
	private final BuyerAddressRepository buyerAddressRepository;

	public OrderManagerImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			BuyerRepository buyerRepository, BuyerAddressRepository buyerAddressRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.buyerRepository = buyerRepository;
		this.buyerAddressRepository = buyerAddressRepository;
	}

	@Override
	public List<OrderResponse> getAllOrders(String sort) {
		return getOrders(sort).stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public OrderResponse getByOrderNr(Long orderNr) {
		Order order = this.orderRepository.findOrderByOrderNr(orderNr)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
		return toResponse(order);
	}

	@Override
	@Transactional
	public OrderResponse create(CreateOrderRequest request) {
		validate(request);

		Buyer buyer = saveBuyer(request.buyer());
		BuyerAddress deliveryAddress = saveAddress(request.deliveryAddress());
		BigDecimal totalPrice = calculateTotalPrice(request.items());

		Order order = new Order();
		order.setBuyerId(buyer.getBuyerId());
		order.setOrderStatus(OrderStatus.WAITING_FOR_CONFIRMATION);
		order.setOrderTime(LocalDateTime.now());
		order.setPaymentOption(request.paymentOption());
		order.setDeliveryAddressId(deliveryAddress.getBuyerAddressId());
		order.setContactNumber(request.contactNumber());
		order.setNote(request.note());
		order.setCurrency(normalizeCurrency(request.currency()));
		order.setTotalPrice(totalPrice);

		Order savedOrder = this.orderRepository.save(order);
		saveItems(savedOrder.getOrderNr(), request.items());

		return getByOrderNr(savedOrder.getOrderNr());
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(Long orderNr, UpdateOrderStatusRequest request) {
		if (request == null || request.orderStatus() == null) {
			throw badRequest("Order status is required");
		}

		Order order = this.orderRepository.findOrderByOrderNr(orderNr)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
		order.setOrderStatus(request.orderStatus());

		Order savedOrder = this.orderRepository.save(order);
		return getByOrderNr(savedOrder.getOrderNr());
	}

	private List<Order> getOrders(String sort) {
		if ("totalPrice".equalsIgnoreCase(sort) || "total_price".equalsIgnoreCase(sort)) {
			return this.orderRepository.findAllOrdersByTotalPriceAsc();
		}
		if ("-totalPrice".equalsIgnoreCase(sort) || "totalPrice,desc".equalsIgnoreCase(sort)) {
			return this.orderRepository.findAllOrdersByTotalPriceDesc();
		}
		return this.orderRepository.findAllOrders();
	}

	private void validate(CreateOrderRequest request) {
		if (request == null) {
			throw badRequest("Order request is required");
		}
		validateBuyer(request.buyer());
		validateAddress(request.deliveryAddress());
		if (request.paymentOption() == null) {
			throw badRequest("Payment option is required");
		}
		if (isBlank(request.contactNumber())) {
			throw badRequest("Contact number is required");
		}
		if (isBlank(request.currency())) {
			throw badRequest("Currency is required");
		}
		if (request.items() == null || request.items().isEmpty()) {
			throw badRequest("At least one order item is required");
		}
		for (CreateOrderItemRequest item : request.items()) {
			validateItem(item);
		}
	}

	private void validateBuyer(CreateBuyerRequest buyer) {
		if (buyer == null) {
			throw badRequest("Buyer is required");
		}
		if (isBlank(buyer.firstName())) {
			throw badRequest("Buyer first name is required");
		}
		if (isBlank(buyer.lastName())) {
			throw badRequest("Buyer last name is required");
		}
	}

	private void validateAddress(CreateAddressRequest address) {
		if (address == null) {
			throw badRequest("Delivery address is required");
		}
		if (isBlank(address.city())) {
			throw badRequest("Delivery city is required");
		}
		if (isBlank(address.street())) {
			throw badRequest("Delivery street is required");
		}
	}

	private void validateItem(CreateOrderItemRequest item) {
		if (item == null) {
			throw badRequest("Order item is required");
		}
		if (isBlank(item.name())) {
			throw badRequest("Order item name is required");
		}
		if (item.quantity() == null || item.quantity() <= 0) {
			throw badRequest("Order item quantity must be greater than zero");
		}
		if (item.price() == null || item.price().compareTo(BigDecimal.ZERO) <= 0) {
			throw badRequest("Order item price must be greater than zero");
		}
	}

	private Buyer saveBuyer(CreateBuyerRequest request) {
		Buyer buyer = new Buyer();
		buyer.setFirstName(request.firstName());
		buyer.setLastName(request.lastName());
		buyer.setTitle(request.title());
		return this.buyerRepository.save(buyer);
	}

	private BuyerAddress saveAddress(CreateAddressRequest request) {
		BuyerAddress address = new BuyerAddress();
		address.setCity(request.city());
		address.setStreet(request.street());
		address.setHomeNumber(request.homeNumber());
		return this.buyerAddressRepository.save(address);
	}

	private BigDecimal calculateTotalPrice(List<CreateOrderItemRequest> items) {
		return items.stream()
				.map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private void saveItems(Long orderNr, List<CreateOrderItemRequest> items) {
		short itemNr = 1;
		for (CreateOrderItemRequest request : items) {
			OrderItem item = new OrderItem();
			item.setOrderId(orderNr);
			item.setItemNr(itemNr++);
			item.setName(request.name());
			item.setQuantity(request.quantity());
			item.setPrice(request.price());
			this.orderItemRepository.save(item);
		}
	}

	private OrderResponse toResponse(Order order) {
		Buyer buyer = this.buyerRepository.findById(order.getBuyerId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Buyer not found"));
		BuyerAddress address = this.buyerAddressRepository.findById(order.getDeliveryAddressId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delivery address not found"));
		List<OrderItemResponse> items = this.orderItemRepository.findByOrderNr(order.getOrderNr()).stream()
				.map(this::toResponse)
				.toList();

		return new OrderResponse(
				order.getOrderNr(),
				buyer.getBuyerId(),
				formatBuyerName(buyer),
				order.getOrderStatus(),
				order.getOrderTime(),
				order.getPaymentOption(),
				toResponse(address),
				order.getContactNumber(),
				order.getNote(),
				items,
				order.getCurrency(),
				order.getTotalPrice());
	}

	private OrderItemResponse toResponse(OrderItem item) {
		return new OrderItemResponse(
				item.getOrderItemId(),
				item.getItemNr(),
				item.getName(),
				item.getQuantity(),
				item.getPrice());
	}

	private AddressResponse toResponse(BuyerAddress address) {
		return new AddressResponse(
				address.getBuyerAddressId(),
				address.getCity(),
				address.getStreet(),
				address.getHomeNumber());
	}

	private String formatBuyerName(Buyer buyer) {
		if (buyer.getTitle() == null || buyer.getTitle().isBlank()) {
			return buyer.getFirstName() + " " + buyer.getLastName();
		}
		return buyer.getTitle() + " " + buyer.getFirstName() + " " + buyer.getLastName();
	}

	private String normalizeCurrency(String currency) {
		return currency.trim().toUpperCase();
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	private ResponseStatusException badRequest(String reason) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
	}
}

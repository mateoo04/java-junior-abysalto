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
import hr.abysalto.hiring.api.junior.model.MenuItem;
import hr.abysalto.hiring.api.junior.model.Order;
import hr.abysalto.hiring.api.junior.model.OrderItem;
import hr.abysalto.hiring.api.junior.model.OrderStatus;
import hr.abysalto.hiring.api.junior.repository.BuyerAddressRepository;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import hr.abysalto.hiring.api.junior.repository.MenuItemRepository;
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
	private final MenuItemRepository menuItemRepository;

	public OrderManagerImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			BuyerRepository buyerRepository, BuyerAddressRepository buyerAddressRepository,
			MenuItemRepository menuItemRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.buyerRepository = buyerRepository;
		this.buyerAddressRepository = buyerAddressRepository;
		this.menuItemRepository = menuItemRepository;
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
		Buyer buyer = resolveBuyer(request);
		BuyerAddress deliveryAddress = saveAddress(request.deliveryAddress());
		List<ResolvedOrderLine> lines = resolveOrderLines(request.items());
		BigDecimal totalPrice = calculateTotalPrice(lines);

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
		saveItems(savedOrder.getOrderNr(), lines);

		return getByOrderNr(savedOrder.getOrderNr());
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(Long orderNr, UpdateOrderStatusRequest request) {
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

	private Buyer resolveBuyer(CreateOrderRequest request) {
		if (request.buyerId() != null) {
			return this.buyerRepository.findById(request.buyerId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buyer not found"));
		}
		return saveBuyer(request.buyer());
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

	private List<ResolvedOrderLine> resolveOrderLines(List<CreateOrderItemRequest> items) {
		return items.stream()
				.map(this::resolveOrderLine)
				.toList();
	}

	private ResolvedOrderLine resolveOrderLine(CreateOrderItemRequest request) {
		MenuItem menuItem = this.menuItemRepository.findById(request.menuItemId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
		if (!Boolean.TRUE.equals(menuItem.getActive())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item is not available: " + menuItem.getName());
		}
		return new ResolvedOrderLine(
				menuItem.getMenuItemId(),
				menuItem.getName(),
				request.quantity(),
				menuItem.getPrice());
	}

	private BigDecimal calculateTotalPrice(List<ResolvedOrderLine> lines) {
		return lines.stream()
				.map(line -> line.price().multiply(BigDecimal.valueOf(line.quantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private void saveItems(Long orderNr, List<ResolvedOrderLine> lines) {
		short itemNr = 1;
		for (ResolvedOrderLine line : lines) {
			OrderItem item = new OrderItem();
			item.setOrderId(orderNr);
			item.setItemNr(itemNr++);
			item.setName(line.name());
			item.setQuantity(line.quantity());
			item.setPrice(line.price());
			item.setMenuItemId(line.menuItemId());
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
}

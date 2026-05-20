package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.AddressResponse;
import hr.abysalto.hiring.api.junior.dto.OrderItemResponse;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.model.Buyer;
import hr.abysalto.hiring.api.junior.model.BuyerAddress;
import hr.abysalto.hiring.api.junior.model.Order;
import hr.abysalto.hiring.api.junior.model.OrderItem;
import hr.abysalto.hiring.api.junior.repository.BuyerAddressRepository;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import hr.abysalto.hiring.api.junior.repository.OrderItemRepository;
import hr.abysalto.hiring.api.junior.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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

	private List<Order> getOrders(String sort) {
		if ("totalPrice".equalsIgnoreCase(sort) || "total_price".equalsIgnoreCase(sort)) {
			return this.orderRepository.findAllOrdersByTotalPriceAsc();
		}
		if ("-totalPrice".equalsIgnoreCase(sort) || "totalPrice,desc".equalsIgnoreCase(sort)) {
			return this.orderRepository.findAllOrdersByTotalPriceDesc();
		}
		return this.orderRepository.findAllOrders();
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
}

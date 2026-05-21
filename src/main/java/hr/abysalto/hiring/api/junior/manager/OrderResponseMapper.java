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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class OrderResponseMapper {
	private final BuyerRepository buyerRepository;
	private final BuyerAddressRepository buyerAddressRepository;
	private final OrderItemRepository orderItemRepository;

	public OrderResponseMapper(BuyerRepository buyerRepository, BuyerAddressRepository buyerAddressRepository,
			OrderItemRepository orderItemRepository) {
		this.buyerRepository = buyerRepository;
		this.buyerAddressRepository = buyerAddressRepository;
		this.orderItemRepository = orderItemRepository;
	}

	public OrderResponse toResponse(Order order) {
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

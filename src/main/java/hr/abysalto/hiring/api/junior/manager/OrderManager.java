package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;

import java.util.List;

public interface OrderManager {
	List<OrderResponse> getAllOrders(String sort);
	OrderResponse getByOrderNr(Long orderNr);
	OrderResponse create(CreateOrderRequest request);
}

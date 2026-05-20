package hr.abysalto.hiring.api.junior.controller;

import hr.abysalto.hiring.api.junior.dto.CreateOrderRequest;
import hr.abysalto.hiring.api.junior.dto.OrderResponse;
import hr.abysalto.hiring.api.junior.dto.UpdateOrderStatusRequest;
import hr.abysalto.hiring.api.junior.manager.OrderManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Orders", description = "for handling restaurant orders")
@RestController
@RequestMapping("orders")
public class OrderController {
	private final OrderManager orderManager;

	public OrderController(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@GetMapping
	public List<OrderResponse> list(@RequestParam(required = false) String sort) {
		return this.orderManager.getAllOrders(sort);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse create(@RequestBody CreateOrderRequest request) {
		return this.orderManager.create(request);
	}

	@GetMapping("/{orderNr}")
	public OrderResponse getByOrderNr(@PathVariable Long orderNr) {
		return this.orderManager.getByOrderNr(orderNr);
	}

	@PatchMapping("/{orderNr}/status")
	public OrderResponse updateStatus(@PathVariable Long orderNr, @RequestBody UpdateOrderStatusRequest request) {
		return this.orderManager.updateStatus(orderNr, request);
	}
}

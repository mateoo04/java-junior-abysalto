package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.OrderStatus;

public record UpdateOrderStatusRequest(OrderStatus orderStatus) {
}

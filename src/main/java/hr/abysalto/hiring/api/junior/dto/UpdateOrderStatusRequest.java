package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus orderStatus) {
}

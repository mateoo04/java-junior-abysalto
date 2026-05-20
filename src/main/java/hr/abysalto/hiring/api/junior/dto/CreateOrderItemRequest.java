package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
		String name,
		Short quantity,
		BigDecimal price
) {
}

package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
		Long orderItemId,
		Short itemNr,
		String name,
		Short quantity,
		BigDecimal price
) {
}

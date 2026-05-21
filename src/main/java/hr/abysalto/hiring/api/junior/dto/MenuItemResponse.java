package hr.abysalto.hiring.api.junior.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
		Long menuItemId,
		String name,
		BigDecimal price,
		boolean active
) {
}

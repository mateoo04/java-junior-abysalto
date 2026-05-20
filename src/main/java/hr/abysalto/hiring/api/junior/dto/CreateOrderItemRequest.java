package hr.abysalto.hiring.api.junior.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemRequest(
		@NotBlank
		String name,
		@NotNull
		@Positive
		Short quantity,
		@NotNull
		@Positive
		BigDecimal price
) {
}

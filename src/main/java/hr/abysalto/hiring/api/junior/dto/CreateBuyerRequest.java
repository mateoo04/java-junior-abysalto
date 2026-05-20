package hr.abysalto.hiring.api.junior.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBuyerRequest(
		@NotBlank
		String firstName,
		@NotBlank
		String lastName,
		String title
) {
}

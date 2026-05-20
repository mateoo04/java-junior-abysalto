package hr.abysalto.hiring.api.junior.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(
		@NotBlank
		String city,
		@NotBlank
		String street,
		String homeNumber
) {
}

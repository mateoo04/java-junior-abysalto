package hr.abysalto.hiring.api.junior.dto;

public record CreateAddressRequest(
		String city,
		String street,
		String homeNumber
) {
}

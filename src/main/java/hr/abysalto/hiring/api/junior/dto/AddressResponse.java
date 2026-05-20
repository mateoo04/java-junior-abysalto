package hr.abysalto.hiring.api.junior.dto;

public record AddressResponse(
		Long buyerAddressId,
		String city,
		String street,
		String homeNumber
) {
}

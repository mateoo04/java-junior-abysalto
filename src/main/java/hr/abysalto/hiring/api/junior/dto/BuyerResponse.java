package hr.abysalto.hiring.api.junior.dto;

public record BuyerResponse(
		Long buyerId,
		String firstName,
		String lastName,
		String title
) {
}

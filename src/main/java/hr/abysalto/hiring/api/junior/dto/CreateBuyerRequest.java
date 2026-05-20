package hr.abysalto.hiring.api.junior.dto;

public record CreateBuyerRequest(
		String firstName,
		String lastName,
		String title
) {
}

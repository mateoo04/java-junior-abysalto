package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.PaymentOption;

public record BuyerOrderDefaultsResponse(
		String contactNumber,
		PaymentOption paymentOption,
		String currency,
		AddressResponse deliveryAddress
) {
}

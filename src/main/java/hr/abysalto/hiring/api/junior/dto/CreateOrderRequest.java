package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.PaymentOption;

import java.util.List;

public record CreateOrderRequest(
		CreateBuyerRequest buyer,
		PaymentOption paymentOption,
		CreateAddressRequest deliveryAddress,
		String contactNumber,
		String note,
		String currency,
		List<CreateOrderItemRequest> items
) {
}

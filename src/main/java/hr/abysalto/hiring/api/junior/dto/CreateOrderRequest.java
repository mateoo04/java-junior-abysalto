package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.PaymentOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
		@NotNull
		@Valid
		CreateBuyerRequest buyer,
		@NotNull
		PaymentOption paymentOption,
		@NotNull
		@Valid
		CreateAddressRequest deliveryAddress,
		@NotBlank
		String contactNumber,
		String note,
		@NotBlank
		String currency,
		@NotEmpty
		List<@Valid CreateOrderItemRequest> items
) {
}

package hr.abysalto.hiring.api.junior.dto;

import hr.abysalto.hiring.api.junior.model.OrderStatus;
import hr.abysalto.hiring.api.junior.model.PaymentOption;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
		Long orderNr,
		Long buyerId,
		String buyerName,
		OrderStatus orderStatus,
		LocalDateTime orderTime,
		PaymentOption paymentOption,
		AddressResponse deliveryAddress,
		String contactNumber,
		String note,
		List<OrderItemResponse> items,
		String currency,
		BigDecimal totalPrice
) {
}

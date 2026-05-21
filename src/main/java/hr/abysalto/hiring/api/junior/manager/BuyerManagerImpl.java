package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.AddressResponse;
import hr.abysalto.hiring.api.junior.dto.BuyerOrderDefaultsResponse;
import hr.abysalto.hiring.api.junior.dto.BuyerResponse;
import hr.abysalto.hiring.api.junior.model.Buyer;
import hr.abysalto.hiring.api.junior.model.BuyerAddress;
import hr.abysalto.hiring.api.junior.model.Order;
import hr.abysalto.hiring.api.junior.repository.BuyerAddressRepository;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import hr.abysalto.hiring.api.junior.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BuyerManagerImpl implements BuyerManager {

	private static final int MIN_QUERY_LENGTH = 2;

	private final BuyerRepository buyerRepository;
	private final BuyerAddressRepository buyerAddressRepository;
	private final OrderRepository orderRepository;

	public BuyerManagerImpl(BuyerRepository buyerRepository, BuyerAddressRepository buyerAddressRepository,
			OrderRepository orderRepository) {
		this.buyerRepository = buyerRepository;
		this.buyerAddressRepository = buyerAddressRepository;
		this.orderRepository = orderRepository;
	}

	@Override
	public List<BuyerResponse> search(String firstName, String lastName) {
		String first = normalizeQueryParam(firstName);
		String last = normalizeQueryParam(lastName);

		if (first == null && last == null) {
			return List.of();
		}

		return this.buyerRepository.searchByName(first, last).stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public Optional<BuyerOrderDefaultsResponse> getOrderDefaults(Long buyerId) {
		return this.orderRepository.findLatestByBuyerId(buyerId)
				.flatMap(this::toDefaultsResponse);
	}

	private String normalizeQueryParam(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		if (trimmed.length() < MIN_QUERY_LENGTH) {
			return null;
		}
		return trimmed;
	}

	private BuyerResponse toResponse(Buyer buyer) {
		return new BuyerResponse(
				buyer.getBuyerId(),
				buyer.getFirstName(),
				buyer.getLastName(),
				buyer.getTitle()
		);
	}

	private Optional<BuyerOrderDefaultsResponse> toDefaultsResponse(Order order) {
		return this.buyerAddressRepository.findById(order.getDeliveryAddressId())
				.map(address -> new BuyerOrderDefaultsResponse(
						order.getContactNumber(),
						order.getPaymentOption(),
						order.getCurrency(),
						toResponse(address)
				));
	}

	private AddressResponse toResponse(BuyerAddress address) {
		return new AddressResponse(
				address.getBuyerAddressId(),
				address.getCity(),
				address.getStreet(),
				address.getHomeNumber());
	}
}

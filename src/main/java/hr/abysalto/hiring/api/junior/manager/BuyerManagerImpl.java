package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.BuyerResponse;
import hr.abysalto.hiring.api.junior.model.Buyer;
import hr.abysalto.hiring.api.junior.repository.BuyerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuyerManagerImpl implements BuyerManager {

	private static final int MIN_QUERY_LENGTH = 2;

	private final BuyerRepository buyerRepository;

	public BuyerManagerImpl(BuyerRepository buyerRepository) {
		this.buyerRepository = buyerRepository;
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
}

package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.BuyerOrderDefaultsResponse;
import hr.abysalto.hiring.api.junior.dto.BuyerResponse;

import java.util.List;
import java.util.Optional;

public interface BuyerManager {

	List<BuyerResponse> search(String firstName, String lastName);

	Optional<BuyerOrderDefaultsResponse> getOrderDefaults(Long buyerId);
}

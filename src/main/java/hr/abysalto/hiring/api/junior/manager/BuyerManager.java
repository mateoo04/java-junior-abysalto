package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.BuyerResponse;

import java.util.List;

public interface BuyerManager {

	List<BuyerResponse> search(String firstName, String lastName);
}

package hr.abysalto.hiring.api.junior.controller;

import hr.abysalto.hiring.api.junior.dto.BuyerOrderDefaultsResponse;
import hr.abysalto.hiring.api.junior.dto.BuyerResponse;
import hr.abysalto.hiring.api.junior.manager.BuyerManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Buyers", description = "for searching existing buyers")
@RestController
@RequestMapping("buyers")
public class BuyerController {

	private final BuyerManager buyerManager;

	public BuyerController(BuyerManager buyerManager) {
		this.buyerManager = buyerManager;
	}

	@GetMapping("/search")
	public List<BuyerResponse> search(
			@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName) {
		return this.buyerManager.search(firstName, lastName);
	}

	@GetMapping("/{buyerId}/order-defaults")
	public ResponseEntity<BuyerOrderDefaultsResponse> getOrderDefaults(@PathVariable Long buyerId) {
		return this.buyerManager.getOrderDefaults(buyerId)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}
}

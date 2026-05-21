package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.CreateMenuItemRequest;
import hr.abysalto.hiring.api.junior.dto.MenuItemResponse;
import hr.abysalto.hiring.api.junior.model.MenuItem;
import hr.abysalto.hiring.api.junior.repository.MenuItemRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class MenuItemManagerImpl implements MenuItemManager {

	private static final int MIN_SEARCH_LENGTH = 2;

	private final MenuItemRepository menuItemRepository;

	public MenuItemManagerImpl(MenuItemRepository menuItemRepository) {
		this.menuItemRepository = menuItemRepository;
	}

	@Override
	public List<MenuItemResponse> findAllActive() {
		return this.menuItemRepository.findAllActive().stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public List<MenuItemResponse> search(String name) {
		String query = normalizeSearch(name);
		if (query == null) {
			return List.of();
		}
		return this.menuItemRepository.searchByName(query).stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public MenuItemResponse create(CreateMenuItemRequest request) {
		MenuItem menuItem = new MenuItem();
		menuItem.setName(request.name().trim());
		menuItem.setPrice(request.price());
		menuItem.setActive(true);
		try {
			MenuItem saved = this.menuItemRepository.save(menuItem);
			return toResponse(saved);
		} catch (DuplicateKeyException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A meal with this name already exists");
		}
	}

	private String normalizeSearch(String name) {
		if (name == null) {
			return null;
		}
		String trimmed = name.trim();
		if (trimmed.length() < MIN_SEARCH_LENGTH) {
			return null;
		}
		return trimmed;
	}

	private MenuItemResponse toResponse(MenuItem menuItem) {
		return new MenuItemResponse(
				menuItem.getMenuItemId(),
				menuItem.getName(),
				menuItem.getPrice(),
				Boolean.TRUE.equals(menuItem.getActive())
		);
	}
}

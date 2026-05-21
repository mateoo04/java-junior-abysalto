package hr.abysalto.hiring.api.junior.manager;

import hr.abysalto.hiring.api.junior.dto.CreateMenuItemRequest;
import hr.abysalto.hiring.api.junior.dto.MenuItemResponse;

import java.util.List;

public interface MenuItemManager {

	List<MenuItemResponse> findAllActive();

	List<MenuItemResponse> search(String name);

	MenuItemResponse create(CreateMenuItemRequest request);
}

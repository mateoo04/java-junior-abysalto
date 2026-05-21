package hr.abysalto.hiring.api.junior.controller;

import hr.abysalto.hiring.api.junior.dto.CreateMenuItemRequest;
import hr.abysalto.hiring.api.junior.dto.MenuItemResponse;
import hr.abysalto.hiring.api.junior.manager.MenuItemManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Menu", description = "restaurant menu meals")
@RestController
@RequestMapping("menu-items")
public class MenuItemController {

	private final MenuItemManager menuItemManager;

	public MenuItemController(MenuItemManager menuItemManager) {
		this.menuItemManager = menuItemManager;
	}

	@GetMapping
	public List<MenuItemResponse> list() {
		return this.menuItemManager.findAllActive();
	}

	@GetMapping("/search")
	public List<MenuItemResponse> search(@RequestParam(required = false) String name) {
		return this.menuItemManager.search(name);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MenuItemResponse create(@Valid @RequestBody CreateMenuItemRequest request) {
		return this.menuItemManager.create(request);
	}
}

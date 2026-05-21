package hr.abysalto.hiring.api.junior.model;

import java.math.BigDecimal;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class MenuItem {
	@Id
	private Long menuItemId;
	private String name;
	private BigDecimal price;
	private Boolean active;
}

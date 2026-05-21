package hr.abysalto.hiring.api.junior.model;

import java.math.BigDecimal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("ORDER_ITEM")
public class OrderItem {
	@Id
	private Long orderItemId;
	@Column("ORDER_NR")
	private Long orderId;
	private Short itemNr;
	private String name;
	private Short quantity;
	private BigDecimal price;
	@Column("menu_item_id")
	private Long menuItemId;
}

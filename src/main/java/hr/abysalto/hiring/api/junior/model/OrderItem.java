package hr.abysalto.hiring.api.junior.model;

import java.math.BigDecimal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("order_item")
public class OrderItem {
	@Id
	private Long orderItemId;
	@Column("order_nr")
	private Long orderId;
	private Short itemNr;
	private String name;
	private Short quantity;
	private BigDecimal price;
}

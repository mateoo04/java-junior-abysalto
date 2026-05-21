package hr.abysalto.hiring.api.junior.manager;

import java.math.BigDecimal;

record ResolvedOrderLine(
		Long menuItemId,
		String name,
		Short quantity,
		BigDecimal price
) {
}

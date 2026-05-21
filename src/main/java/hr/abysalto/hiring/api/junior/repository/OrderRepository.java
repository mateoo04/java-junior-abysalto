package hr.abysalto.hiring.api.junior.repository;

import hr.abysalto.hiring.api.junior.model.Order;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

	@Query("SELECT * FROM orders ORDER BY order_time DESC")
	List<Order> findAllOrders();

	@Query("SELECT * FROM orders ORDER BY total_price ASC")
	List<Order> findAllOrdersByTotalPriceAsc();

	@Query("SELECT * FROM orders ORDER BY total_price DESC")
	List<Order> findAllOrdersByTotalPriceDesc();

	@Query("SELECT * FROM orders WHERE order_nr = :orderNr")
	Optional<Order> findOrderByOrderNr(@Param("orderNr") Long orderNr);

	@Query("SELECT * FROM orders WHERE buyer_id = :buyerId ORDER BY order_time DESC LIMIT 1")
	Optional<Order> findLatestByBuyerId(@Param("buyerId") Long buyerId);
}

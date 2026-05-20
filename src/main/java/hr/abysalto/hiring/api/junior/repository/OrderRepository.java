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

	@Query("SELECT * FROM \"order\" ORDER BY order_time DESC")
	List<Order> findAllOrders();

	@Query("SELECT * FROM \"order\" ORDER BY total_price ASC")
	List<Order> findAllOrdersByTotalPriceAsc();

	@Query("SELECT * FROM \"order\" ORDER BY total_price DESC")
	List<Order> findAllOrdersByTotalPriceDesc();

	@Query("SELECT * FROM \"order\" WHERE order_nr = :orderNr")
	Optional<Order> findOrderByOrderNr(@Param("orderNr") Long orderNr);
}

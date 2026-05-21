package hr.abysalto.hiring.api.junior.repository;

import hr.abysalto.hiring.api.junior.model.MenuItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends CrudRepository<MenuItem, Long> {

	@Query("SELECT * FROM menu_item WHERE active = TRUE ORDER BY name")
	List<MenuItem> findAllActive();

	@Query("""
			SELECT * FROM menu_item
			WHERE active = TRUE AND LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))
			ORDER BY name
			LIMIT 10
			""")
	List<MenuItem> searchByName(@Param("name") String name);
}

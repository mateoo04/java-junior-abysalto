package hr.abysalto.hiring.api.junior.repository;

import hr.abysalto.hiring.api.junior.model.Buyer;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyerRepository extends CrudRepository<Buyer, Long> { //PagingAndSortingRepository

	@Modifying
	@Query("UPDATE buyer SET first_name = :name WHERE buyer_id = :id")
	boolean updateByFirstName(@Param("id") Long id, @Param("name") String name);

	@Query("""
			SELECT * FROM buyer
			WHERE (:firstName IS NULL OR LOWER(first_name) LIKE LOWER(CONCAT('%', :firstName, '%')))
			  AND (:lastName IS NULL OR LOWER(last_name) LIKE LOWER(CONCAT('%', :lastName, '%')))
			ORDER BY last_name, first_name
			LIMIT 10
			""")
	List<Buyer> searchByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}

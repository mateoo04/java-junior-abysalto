package hr.abysalto.hiring.api.junior.repository;

import hr.abysalto.hiring.api.junior.model.BuyerAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerAddressRepository extends CrudRepository<BuyerAddress, Long> {
}

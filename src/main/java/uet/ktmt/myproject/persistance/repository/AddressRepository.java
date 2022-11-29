package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}

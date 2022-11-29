package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Property findByName(String item);
}

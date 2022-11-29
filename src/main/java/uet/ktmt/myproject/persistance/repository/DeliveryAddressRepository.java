package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Query(value = "select * from delivery_address where order_id = :orderId ", nativeQuery = true)
    Optional<DeliveryAddress> findByOrderId(@Param(value = "orderId")long orderId);
}

package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.ImageProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageProductRepository extends JpaRepository<ImageProduct, Long> {
}

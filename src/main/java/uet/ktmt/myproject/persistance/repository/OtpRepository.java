package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByCellphone(String cellphone);
}

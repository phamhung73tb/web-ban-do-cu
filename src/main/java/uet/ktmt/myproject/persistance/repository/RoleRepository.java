package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.common.myEnum.RoleEnum;
import uet.ktmt.myproject.persistance.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
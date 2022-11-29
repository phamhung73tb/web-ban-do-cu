package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByCellphone(String cellphone);

    @Query(value = "select * " +
            "from user " +
            "where user.username != 'admin' " +
            "order by id desc", nativeQuery = true)
    Page<User> findAllUser(Pageable pageable);

    @Query(value = "select * " +
            "from user " +
            "where concat_ws('', username, lower(full_name), cellphone, email) like %:keyword% " +
            "and user.username != 'admin' " +
            "order by id desc", nativeQuery = true)
    Page<User> findAllUserAndSearch(Pageable pageable, @Param(value = "keyword") String keyword);

    @Query(value = "select count(*) from user where (date(created_date) between :from and :to) and username != 'admin'", nativeQuery = true)
    int getNewUser(@Param(value = "from") String from, @Param(value = "to") String to);

//    @Query(value = "select case when count(*) > 0 then 'true' else 'false' end result " +
//            "from user u " +
//            "where u.username = :username and hidden_flag = 1 ", nativeQuery = true)
//    boolean checkLocked(String username);
}

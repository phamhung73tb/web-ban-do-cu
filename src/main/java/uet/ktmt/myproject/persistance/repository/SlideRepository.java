package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Long> {
    @Query(value="select * from slide where hidden_flag = 0", nativeQuery = true)
    List<Slide> getAllActive();
}

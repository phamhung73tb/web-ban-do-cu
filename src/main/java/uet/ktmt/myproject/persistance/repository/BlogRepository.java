package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query(value="select * from blog where hidden_flag = 0", nativeQuery = true)
    Page<Blog> getAllActive(Pageable pageable);
    @Query(value="select * from blog where hidden_flag = 0 order by id desc limit 4", nativeQuery = true)
    List<Blog> getTop4();
}

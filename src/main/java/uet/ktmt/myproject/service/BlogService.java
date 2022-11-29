package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface BlogService {
    Page<Blog> getAllActive(int page);
    
    void create(Blog blog, MultipartFile multipartFile) throws IOException;

    void edit(long id, Blog blog, MultipartFile multipartFile) throws Throwable;

    void delete(long id) throws Throwable;

    void updateStatusHiddenFlag(long id) throws Throwable;

    Page<Blog> getAll(int page);

    Blog getDetail(long blogId) throws Throwable;

    List<Blog> getTop4();
}

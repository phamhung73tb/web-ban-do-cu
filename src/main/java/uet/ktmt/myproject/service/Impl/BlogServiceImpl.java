package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.persistance.entity.Blog;
import uet.ktmt.myproject.persistance.repository.BlogRepository;
import uet.ktmt.myproject.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Component
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;
    static final String UPLOAD_DIR_BLOG = "src/main/resources/static/blog/0/";

    public Page<Blog> getAllActive(int page) {
        Pageable pageable = PageRequest.of(page, 3);
        return blogRepository.getAllActive(pageable);
    }

    @Transactional
    public void create(Blog blog, MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String typeOfFile = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "blog-" + System.currentTimeMillis() + typeOfFile;
        FileUploadUtil.saveFile(UPLOAD_DIR_BLOG, fileName, multipartFile);
        blog.setImage(fileName);
        blogRepository.save(blog);
    }

    @Transactional
    public void edit(long id, Blog blog, MultipartFile multipartFile) throws Throwable {
        Blog foundBlog = blogRepository.findById(id).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy blog !!!");
                }
        );
        foundBlog.setTitle(blog.getTitle());
        foundBlog.setContent(blog.getContent());

        if (!multipartFile.isEmpty()) {
            FileUploadUtil.deleteFile(UPLOAD_DIR_BLOG, foundBlog.getImage());
            String originalFilename = multipartFile.getOriginalFilename();
            String typeOfFile = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "blog-" + System.currentTimeMillis() + typeOfFile;
            FileUploadUtil.saveFile(UPLOAD_DIR_BLOG, fileName, multipartFile);
            foundBlog.setImage(fileName);
        }
        blogRepository.save(foundBlog);
    }

    @Transactional
    public void delete(long id) throws Throwable {
        Blog foundBlog = blogRepository.findById(id).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy blog !!!");
                }
        );
        FileUploadUtil.deleteFile(UPLOAD_DIR_BLOG, foundBlog.getImage());
        blogRepository.delete(foundBlog);
    }

    @Transactional
    public void updateStatusHiddenFlag(long id) throws Throwable{
        Blog foundBlog = blogRepository.findById(id).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy blog !!!");
                }
        );
        foundBlog.setHiddenFlag(!foundBlog.getHiddenFlag());
        blogRepository.save(foundBlog);
    }

    public Page<Blog> getAll(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return blogRepository.findAll(pageable);
    }

    public Blog getDetail(long blogId) throws Throwable {
        return blogRepository.findById(blogId).orElseThrow(() -> {
            throw new BadRequestException("Blog không tồn tại !!!");
        });
    }

    public List<Blog> getTop4() {
        return blogRepository.getTop4();
    }
}

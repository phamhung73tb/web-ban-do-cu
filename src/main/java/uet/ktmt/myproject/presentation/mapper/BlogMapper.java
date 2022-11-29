package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.Blog;
import uet.ktmt.myproject.presentation.controller.basic.ImageController;
import uet.ktmt.myproject.presentation.request.BlogRequest;
import uet.ktmt.myproject.presentation.response.BlogResponse;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.time.format.DateTimeFormatter;

public class BlogMapper {
    private BlogMapper() {
        super();
    }

    public static BlogResponse convertToBlogResponse(Blog blog) {
        String apiImage = MvcUriComponentsBuilder.fromMethodName(ImageController.class, "readDetailFile"
                , blog.getClass().getSimpleName().toLowerCase(), "0", blog.getImage()).toUriString();
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .dateTime(blog.getCreatedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .apiGetImage(apiImage)
                .status(blog.getHiddenFlag())
                .build();
    }

    public static Blog convertToBlog(BlogRequest blogRequest) {
        return Blog.builder()
                .id(blogRequest.getId())
                .title(blogRequest.getTitle())
                .content(blogRequest.getContent())
                .hiddenFlag(blogRequest.isStatus())
                .build();
    }
}

package uet.ktmt.myproject.presentation.controller.admin;

import uet.ktmt.myproject.presentation.mapper.BlogMapper;
import uet.ktmt.myproject.presentation.request.BlogRequest;
import uet.ktmt.myproject.presentation.response.BlogResponse;
import uet.ktmt.myproject.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/blog")
public class BlogAdminController {
    @Autowired
    private BlogService blogService;

    @GetMapping("/get-all")
    public String getAll(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        Page<BlogResponse> blogResponseList = blogService.getAll(page - 1)
                .map(BlogMapper::convertToBlogResponse);

        model.addAttribute("blogResponseList", blogResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", blogResponseList.getTotalPages());
        return "admin/admin_list_blog";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("blogRequest", new BlogRequest());
        return "admin/admin_create_blog";
    }

    @PostMapping("/create")
    public String create(Model model, BlogRequest blogRequest
            , @RequestPart(value = "image") MultipartFile multipartFile) throws IOException {

        blogService.create(BlogMapper.convertToBlog(blogRequest), multipartFile);
        return "redirect:/admin/blog/get-all";
    }

    //    new tab
//    <input type="button" value="Open Window" onclick="window.open('http://www.domain.com')">
    @GetMapping("/view/{id}")
    public String blogDetail(Model model, @PathVariable(value = "id") long blogId) throws Throwable {
        BlogResponse blogResponse = BlogMapper.convertToBlogResponse(blogService.getDetail(blogId));
        model.addAttribute("blogResponse", blogResponse);
        return "blog_detail";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(Model model, @PathVariable(value = "id") long blogId) throws Throwable {
        model.addAttribute("blogRequest", new BlogRequest());
        model.addAttribute("blogResponse", BlogMapper.convertToBlogResponse(blogService.getDetail(blogId)));
        return "admin/admin_edit_blog";
    }

    @PostMapping("/edit/{id}")
    public String editBlog(@PathVariable(value = "id") long blogId, BlogRequest blogRequest
            , @RequestPart(value = "image") MultipartFile multipartFile) throws Throwable {
        blogService.edit(
                blogId, BlogMapper.convertToBlog(blogRequest), multipartFile);
        return "redirect:/admin/blog/get-all";
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable(value = "id") long blogId) throws Throwable {
        blogService.updateStatusHiddenFlag(blogId);
        return ResponseEntity.ok().body("Sửa trạng thái ần/hiện blog thành công !!!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") long blogId) throws Throwable {
        blogService.delete(blogId);
        return ResponseEntity.ok().body("Xóa blog thành công !!!");
    }
}

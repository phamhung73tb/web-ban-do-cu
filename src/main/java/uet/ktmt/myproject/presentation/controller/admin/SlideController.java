package uet.ktmt.myproject.presentation.controller.admin;

import uet.ktmt.myproject.presentation.mapper.SlideMapper;
import uet.ktmt.myproject.presentation.request.SlideRequest;
import uet.ktmt.myproject.presentation.response.SlideResponse;
import uet.ktmt.myproject.service.SlideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/slide")
public class SlideController {
    @Autowired
    private SlideService slideService;

    @GetMapping("/get-all")
    public String getAll(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        Page<SlideResponse> slideResponseList = slideService.getAll(page - 1)
                .map(SlideMapper::convertToSlideResponse);

        model.addAttribute("slideResponseList", slideResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", slideResponseList.getTotalPages());
        return "admin/admin_list_slide";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("slideRequest", new SlideRequest());
        return "admin/admin_create_slide";
    }

    @PostMapping("/create")
    public String create(Model model, SlideRequest slideRequest
            , @RequestPart(value = "image") MultipartFile multipartFile) throws IOException {

        slideService.create(SlideMapper.convertToSlide(slideRequest), multipartFile);
        return "redirect:/admin/slide/get-all";
    }

    @GetMapping("/edit/{id}")
    public String getEditPage(Model model, @PathVariable(value = "id") long slideId) throws Throwable {
        model.addAttribute("slideRequest", new SlideRequest());
        model.addAttribute("slideResponse", SlideMapper.convertToSlideResponse(slideService.getDetail(slideId)));
        return "admin/admin_edit_slide";
    }

    @PostMapping("/edit/{id}")
    public String editSlide(@PathVariable(value = "id") long slideId, SlideRequest slideRequest
            , @RequestPart(value = "image") MultipartFile multipartFile) throws Throwable {
        slideService.edit(slideId, SlideMapper.convertToSlide(slideRequest), multipartFile);
        return "redirect:/admin/slide/get-all";
    }

    @PostMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable(value = "id") long slideId) throws Throwable {
        slideService.updateStatusHiddenFlag(slideId);
        return ResponseEntity.ok().body("Sửa trạng thái ần/hiện slide thành công !!!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") long slideId) throws Throwable {
        slideService.delete(slideId);
        return ResponseEntity.ok().body("Xóa slide thành công !!!");
    }
}

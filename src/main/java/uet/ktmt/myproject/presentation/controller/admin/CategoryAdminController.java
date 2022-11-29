package uet.ktmt.myproject.presentation.controller.admin;

import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.presentation.mapper.CategoryMapper;
import uet.ktmt.myproject.presentation.request.CategoryRequest;
import uet.ktmt.myproject.presentation.response.CategoryResponse;
import uet.ktmt.myproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/category")
public class CategoryAdminController {

    @Autowired
    private CategoryService categoryService;

    static final String UPLOAD_DIR_CATEGORY = "src/main/resources/static/category/";

    @GetMapping("/get-all")
    String getAll(Model model) {
        HashMap<CategoryResponse, List<CategoryResponse>> treeCategory = new HashMap<>();
        categoryService.getTreeCategory().forEach((k, v) ->
                treeCategory.put(
                        CategoryMapper.convertToCategoryResponse(k)
                        , v.stream().map(CategoryMapper::convertToCategoryResponse).collect(Collectors.toList())
                )
        );
        model.addAttribute("treeCategory", treeCategory);
        return "admin/admin_list_category";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "admin/admin_new_category";
    }

    @PostMapping("/create")
    public String create(CategoryRequest categoryRequest
            , @RequestParam("image") MultipartFile multipartFile) throws IOException {
        CategoryResponse categoryResponse = CategoryMapper.convertToCategoryResponse(
                categoryService.create(
                        CategoryMapper.convertToCategory(categoryRequest)
                        , multipartFile.getOriginalFilename())
        );
        String uploadDir = UPLOAD_DIR_CATEGORY + categoryResponse.getId();
        String fileName = categoryResponse.getApiGetImage().substring(categoryResponse.getApiGetImage().lastIndexOf("/") + 1);
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        return "redirect:/admin/category/get-all";
    }

    @GetMapping("/view/{id}")
    public String getDetail(Model model, @PathVariable(value = "id") long categoryId) throws Throwable {
        CategoryResponse categoryResponse = CategoryMapper.convertToCategoryResponse(
                categoryService.getDetail(categoryId)
        );
        model.addAttribute("categoryResponse", categoryResponse);
        return "admin/admin_category_detail";
    }

    @GetMapping("/edit/{id}")
    public String getPageEdit(Model model, @PathVariable(value = "id") long categoryId) throws Throwable {
        CategoryResponse categoryResponse = CategoryMapper.convertToCategoryResponse(
                categoryService.getDetail(categoryId)
        );
        model.addAttribute("categoryResponse", categoryResponse);
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "admin/admin_category_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(Model model, @ModelAttribute CategoryRequest categoryRequest
            , @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws Throwable {

        CategoryResponse categoryResponse;
        if (multipartFile.getOriginalFilename().equals("")) {
            categoryResponse = CategoryMapper.convertToCategoryResponse(
                    categoryService.edit(CategoryMapper.convertToCategory(categoryRequest), null)
            );
        } else {
            String uploadDir = UPLOAD_DIR_CATEGORY + categoryRequest.getId();
            String fileName = categoryService.getDetail(categoryRequest.getId()).getImage();
            FileUploadUtil.deleteFile(uploadDir, fileName);
            categoryResponse = CategoryMapper.convertToCategoryResponse(
                    categoryService.edit(
                            CategoryMapper.convertToCategory(categoryRequest)
                            , multipartFile.getOriginalFilename())
            );
            String newFileName = categoryResponse.getApiGetImage()
                    .substring(categoryResponse.getApiGetImage().lastIndexOf("/") + 1);
            FileUploadUtil.saveFile(uploadDir, newFileName, multipartFile);
        }
        model.addAttribute("categoryResponse", categoryResponse);
        return "admin/admin_category_detail";
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") long categoryId) throws Throwable {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().body("Xóa loại sản phẩm thành công !!!");
    }

    @GetMapping("/get-all-not-parent")
    public ResponseEntity<List<CategoryResponse>> getAllNotParent() {
        List<CategoryResponse> categoryResponseList = categoryService.getListCategoryHasNotParent()
                .stream()
                .map(CategoryMapper::convertToCategoryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(categoryResponseList);
    }

    @GetMapping("/get-child/{parentId}")
    public ResponseEntity<List<CategoryResponse>> getChild(@PathVariable(value = "parentId") long parentId) {
        List<CategoryResponse> categoryResponseList = categoryService.getListCategoryChild(parentId)
                .stream()
                .map(CategoryMapper::convertToCategoryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(categoryResponseList);
    }
}

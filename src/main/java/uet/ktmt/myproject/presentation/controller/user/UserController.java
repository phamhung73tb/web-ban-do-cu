package uet.ktmt.myproject.presentation.controller.user;

import uet.ktmt.myproject.common.file.FileUploadUtil;
import uet.ktmt.myproject.presentation.mapper.ProductMapper;
import uet.ktmt.myproject.presentation.mapper.UserMapper;
import uet.ktmt.myproject.presentation.request.LoginRequest;
import uet.ktmt.myproject.presentation.request.UserRequest;
import uet.ktmt.myproject.presentation.response.ProductResponse;
import uet.ktmt.myproject.presentation.response.UserResponse;
import uet.ktmt.myproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    static final String UPLOAD_DIR_USER = "src/main/resources/static/user/";

    @GetMapping("/get-info")
    public String getInfo(Model model) throws Throwable {
        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getCurrentUser());
        model.addAttribute("userResponse", userResponse);
        return "user/info_user";
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editInfo(@RequestBody UserRequest userRequest) throws Throwable {
        userService.changeInfo(UserMapper.convertToUser(userRequest));
        return ResponseEntity.ok().body("Sửa thông tin thành công !!!");
    }

    @GetMapping("/get-full-name")
    public ResponseEntity<String> getFullName(Model model) throws Throwable {
        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getCurrentUser());
        return ResponseEntity.ok().body(userResponse.getFullName());
    }

    @PostMapping("/edit/avatar/{id}")
    public ResponseEntity<String> editAvatar(@PathVariable(value = "id") long userId
            , @RequestPart(value = "image") MultipartFile multipartFile) throws Throwable {
        String uploadDir = UPLOAD_DIR_USER + userId;
        String fileName = userService.getUserById(userId).getAvatar();
        FileUploadUtil.deleteFile(uploadDir, fileName);
        String newFileName = userService.updateAvatar(userId, multipartFile.getOriginalFilename());
        FileUploadUtil.saveFile(uploadDir, newFileName, multipartFile);
        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getUserById(userId));
        return ResponseEntity.ok().body(userResponse.getApiGetAvatar());
    }

    @GetMapping("/change-password")
    public String getPageChangePassword(Model model) throws Throwable {
        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getCurrentUser());
        model.addAttribute("userResponse", userResponse);
        return "user/form_change_password";
    }

    @PostMapping("/check-password")
    public ResponseEntity<String> checkPassword(@RequestBody LoginRequest loginRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Sai mật khẩu !");
        }
        return ResponseEntity.ok().body("Mật khẩu đúng !!!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody LoginRequest loginRequest) throws Throwable {
        userService.changPassword(loginRequest.getPassword());
        return ResponseEntity.ok().body("Đổi mật khẩu thành công !!!");
    }

    @GetMapping("/my-list-product")
    public String getMyListProduct(Model model
            , @RequestParam(value = "page", required = false, defaultValue = "1") int page
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status) throws Throwable {
        Page<ProductResponse> productResponseList = userService.getMyListProduct(page -1, status)
                .map(ProductMapper::convertToProductResponse);
        model.addAttribute("productResponseList", productResponseList.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", productResponseList.getTotalPages());
        model.addAttribute("status", status);
        return "user/my_list_product";
    }

    @PostMapping("/add-wishlist/{id}")
    public ResponseEntity<String> addWishlist(@PathVariable(value = "id") long productId) throws Throwable {
        userService.addWishlist(productId);
        return ResponseEntity.ok().body("Thêm sản phần vào danh sách yêu thích thành công !!!");
    }

    @GetMapping("/get-wishlist")
    public String getWishlist(Model model
            , @RequestParam(value = "page", required = false, defaultValue = "1") int page) throws Throwable {
        Page<ProductResponse> productResponseList = userService.getWishlist(page - 1)
                .map(ProductMapper::convertToProductResponse);
        model.addAttribute("productResponseList", productResponseList);
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", productResponseList.getTotalPages());
        return "user/wishlist";
    }

    @PostMapping("/remove-wishlist/{id}")
    public ResponseEntity<String> removeWishlist(@PathVariable(value = "id") long productId) throws Throwable {
        userService.removeWishlist(productId);
        return ResponseEntity.ok().body("Loại bỏ sản phần từ danh sách yêu thích thành công !!!");
    }
}

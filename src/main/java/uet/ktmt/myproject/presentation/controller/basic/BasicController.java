package uet.ktmt.myproject.presentation.controller.basic;

import org.springframework.security.core.context.SecurityContextHolder;
import uet.ktmt.myproject.common.myEnum.ProductStatusEnum;
import uet.ktmt.myproject.common.otp.RandomOtpUtil;
import uet.ktmt.myproject.common.otp.SmsSender;
import uet.ktmt.myproject.persistance.entity.HistoryClick;
import uet.ktmt.myproject.persistance.repository.HistoryClickRepository;
import uet.ktmt.myproject.presentation.mapper.*;
import uet.ktmt.myproject.presentation.request.LoginRequest;
import uet.ktmt.myproject.presentation.request.OtpRequest;
import uet.ktmt.myproject.presentation.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uet.ktmt.myproject.presentation.response.BlogResponse;
import uet.ktmt.myproject.presentation.response.CategoryResponse;
import uet.ktmt.myproject.presentation.response.ProductResponse;
import uet.ktmt.myproject.presentation.response.SlideResponse;
import uet.ktmt.myproject.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BasicController {

    @Autowired
    private OtpService otpService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SlideService slideService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private HistoryClickRepository historyClickRepository;

    @GetMapping("/home")
    public String home(Model model) throws Throwable {
        HashMap<CategoryResponse, List<CategoryResponse>> menuResponse = new HashMap<>();
        categoryService.getTreeCategory().forEach((k, v) ->
                menuResponse.put(
                        CategoryMapper.convertToCategoryResponse(k)
                        , v.stream().map(CategoryMapper::convertToCategoryResponse).collect(Collectors.toList())
                )
        );
        model.addAttribute("menuResponse", menuResponse);

        List<ProductResponse> randomProductList = productService.getRandomProduct(9)
                .stream()
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("randomProductList", randomProductList);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals("anonymousUser") &&
                productService.getProductRecommendInHomePage(userService.getCurrentUser().getId()).size() > 0) {
            List<ProductResponse> getProductRecommendInHomePage = productService.getProductRecommendInHomePage(userService.getCurrentUser().getId())
                    .stream()
                    .map(ProductMapper::convertToProductResponse)
                    .collect(Collectors.toList());
            model.addAttribute("getProductRecommendInHomePage", getProductRecommendInHomePage);
        } else {
            List<ProductResponse> getProductRecommendInHomePage = productService.getRandomProduct(9)
                    .stream()
                    .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                    .map(ProductMapper::convertToProductResponse)
                    .collect(Collectors.toList());
            model.addAttribute("getProductRecommendInHomePage", getProductRecommendInHomePage);
        }


        List<ProductResponse> newProductList = productService.getNewProduct(6)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("newProductList", newProductList);

        List<ProductResponse> hotProductList = productService.getHotProduct(3)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("hotProductList", hotProductList);

        List<SlideResponse> slideResponseList = slideService.getAllActive()
                .stream()
                .map(SlideMapper::convertToSlideResponse)
                .collect(Collectors.toList());
        model.addAttribute("slideResponseList", slideResponseList);

        List<BlogResponse> blogResponseList = blogService.getTop4()
                .stream()
                .map(BlogMapper::convertToBlogResponse)
                .collect(Collectors.toList());
        model.addAttribute("blogResponseList", blogResponseList);

        List<ProductResponse> randomAllProductList = productService.getRandomAllProduct(12)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("randomAllProductList", randomAllProductList);

        List<ProductResponse> randomQuanAoList = productService.getRandomQuanAo(12)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("randomQuanAoList", randomQuanAoList);

        List<ProductResponse> randomQuanDoDienTuList = productService.getRandomDoDienTu(12)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("randomQuanDoDienTuList", randomQuanDoDienTuList);

        List<ProductResponse> randomSachTruyenList = productService.getRandomSachTruyen(12)
                .stream()
                .filter(product -> product.getStatus() == ProductStatusEnum.STOCKING)
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("randomSachTruyenList", randomSachTruyenList);

        return "index";
    }

    @GetMapping("/")
    public String homeDefault() {
        return "redirect:/home";
    }

    @GetMapping("/generate-otp")
    public String generateOtp(@ModelAttribute(value = "cellphone", binding = false) String cellphone
            , @ModelAttribute(value = "status", binding = false) String status
            , Model model) {
        model.addAttribute("cellphone", cellphone);
        model.addAttribute("status", status);
        return "form_verify_otp";
    }

    @PostMapping("/generate-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) {
        String otp = RandomOtpUtil.createOtp();
        try {
            otpService.addOtp(otp, otpRequest.getCellphone());
            SmsSender.sendOtp(otpRequest.getCellphone(), otp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Gửi thành công !!!");
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Param(value = "cellphone") String cellphone, @Param(value = "otp") String otp
            , RedirectAttributes redirectAttributes
            , Model model) {
        String status = otpService.checkOtp(otp, cellphone);
        if (status.equals("true")) {
            model.addAttribute("userRequest", UserRequest.builder().cellphone(cellphone).build());
            return "form_register";
        } else {
            redirectAttributes.addAttribute("cellphone", cellphone);
            redirectAttributes.addAttribute("status", status);
            return "redirect:/generate-otp";
        }
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute UserRequest userRequest) {
        userService.createUser(UserMapper.convertToUser(userRequest));
        model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("statusRegister", "Đăng ký thành công !!!");
        return "login";
    }

    @PostMapping("/check-username-exist")
    public ResponseEntity<String> checkUsernameExist(@RequestBody UserRequest userRequest) {
        if (userService.existsByUsername(userRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username đã tồn tại !!");
        } else {
            return ResponseEntity.ok().body("Username có thể dùng !!");
        }
    }

    @PostMapping("/check-email-exist")
    public ResponseEntity<String> checkEmailExist(@RequestBody UserRequest userRequest
            , @RequestParam(value = "id", required = false, defaultValue = "0") long userId) throws Throwable {
        if (userService.existsByEmail(userRequest.getEmail(), userId)) {
            return ResponseEntity.badRequest().body("Email đã tồn tại !!");
        } else {
            return ResponseEntity.ok().body("Email có thể dùng !!");
        }
    }

    @PostMapping("/check-cellphone-exist")
    public ResponseEntity<String> checkCellphoneExist(@RequestBody UserRequest userRequest
            , @RequestParam(value = "id", required = false, defaultValue = "0") long userId) throws Throwable {
        if (userService.existsByCellphone(userRequest.getCellphone(), userId)) {
            return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại !!");
        } else {
            return ResponseEntity.ok().body("Số điện thoại có thể dùng !!");
        }
    }

    @GetMapping("/get-menu2")
    public String getMenu2(Model model) {
        HashMap<CategoryResponse, List<CategoryResponse>> menuResponse = new HashMap<>();
        categoryService.getTreeCategory().forEach((k, v) ->
                menuResponse.put(
                        CategoryMapper.convertToCategoryResponse(k)
                        , v.stream().map(CategoryMapper::convertToCategoryResponse).collect(Collectors.toList())
                )
        );
        model.addAttribute("menuResponse", menuResponse);
        return "fragment_menu2";
    }

    @GetMapping("/forget-password")
    public String getPageForgetPassword() {
        return "form_forget_password";
    }

    @PostMapping("/send-new_password-by-email")
    public ResponseEntity<String> forgetPassword(@RequestBody HashMap<String, String> map) throws Throwable {
        String email = map.get("email");
        String newPassword = userService.changPasswordForUserForgetPassword(email);
        emailService.send(email, "Chợ cũ - Lấy lại mật khẩu", "Mật khẩu mới của bạn là: " + newPassword);
        return ResponseEntity.ok().body("Đổi mật khẩu và gửi mail thành công !!!");
    }


    @GetMapping("/product/{slug}")
    public String getDetailProduct(Model model, @PathVariable(value = "slug") String slug) throws Throwable {
        ProductResponse productResponse = ProductMapper.convertToProductResponse(productService.getDetailProductBySlug(slug));
        model.addAttribute("productResponse", productResponse);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals("anonymousUser")) {
            if (historyClickRepository.getByUserIdAndProductId(userService.getCurrentUser().getId(), productResponse.getId()).isPresent()) {
                HistoryClick history = historyClickRepository
                        .getByUserIdAndProductId(userService.getCurrentUser().getId(), productResponse.getId()).get();
                history.setClicking(history.getClicking() + 1);
                historyClickRepository.save(history);
            } else {
                HistoryClick historyClick = new HistoryClick();
                historyClick.setClicking(1L);
                historyClick.setUser(userService.getCurrentUser());
                historyClick.setProduct(productService.getDetailProductBySlug(slug));
            }
        }
        List<ProductResponse> recommendProductList = productService.getRecommendList(productResponse.getId(), 12)
                .stream()
                .map(ProductMapper::convertToProductResponse)
                .collect(Collectors.toList());
        model.addAttribute("recommendProductList", recommendProductList);

        return "product_detail";
    }

    @GetMapping("/category/{slug}")
    public String getListProductByCategory(Model model, @PathVariable(value = "slug") String slug
            , @RequestParam(value = "page", required = false, defaultValue = "1") int page
            , @RequestParam(value = "min", required = false, defaultValue = "0") int min
            , @RequestParam(value = "max", required = false, defaultValue = "30000000") int max
            , @RequestParam(value = "sort", required = false, defaultValue = "created_date") String sort
            , @RequestParam(value = "location", required = false, defaultValue = "0") int codeProvince
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status) throws Throwable {

        HashMap<CategoryResponse, List<CategoryResponse>> menuResponse = new HashMap<>();
        categoryService.getTreeCategory().forEach((k, v) ->
                menuResponse.put(
                        CategoryMapper.convertToCategoryResponse(k)
                        , v.stream().map(CategoryMapper::convertToCategoryResponse).collect(Collectors.toList())
                )
        );
        model.addAttribute("menuResponse", menuResponse);

        Page<ProductResponse> productResponsePage = categoryService.getProductByCategory(slug, page - 1, min, max, sort, codeProvince, status)
                .map(ProductMapper::convertToProductResponse);
        model.addAttribute("list_product", productResponsePage.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", productResponsePage.getTotalPages());
        model.addAttribute("slug", slug);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("sort", sort);
        model.addAttribute("location", codeProvince);
        model.addAttribute("status", status);

        return "list_product_category";
    }

    @GetMapping("/search")
    public String search(Model model
            , @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
            , @RequestParam(value = "category_slug", required = false, defaultValue = "") String slugCategory
            , @RequestParam(value = "page", required = false, defaultValue = "1") int page
            , @RequestParam(value = "min", required = false, defaultValue = "0") int min
            , @RequestParam(value = "max", required = false, defaultValue = "30000000") int max
            , @RequestParam(value = "sort", required = false, defaultValue = "created_date") String sort
            , @RequestParam(value = "location", required = false, defaultValue = "0") int codeProvince
            , @RequestParam(value = "status", required = false, defaultValue = "all") String status) throws Throwable {

        HashMap<CategoryResponse, List<CategoryResponse>> menuResponse = new HashMap<>();
        categoryService.getTreeCategory().forEach((k, v) ->
                menuResponse.put(
                        CategoryMapper.convertToCategoryResponse(k)
                        , v.stream().map(CategoryMapper::convertToCategoryResponse).collect(Collectors.toList())
                )
        );
        model.addAttribute("menuResponse", menuResponse);

        Page<ProductResponse> productResponsePage = productService.searchProduct(keyword, page - 1, slugCategory, min, max, sort, codeProvince, status)
                .map(ProductMapper::convertToProductResponse);

        model.addAttribute("list_product", productResponsePage.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", productResponsePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("category_slug", slugCategory);
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        model.addAttribute("sort", sort);
        model.addAttribute("location", codeProvince);
        model.addAttribute("status", status);

        return "search_product_page";
    }

    @GetMapping("/blog")
    public String blog(Model model, @RequestParam(value = "page", required = false, defaultValue = "1") int page) {

        Page<BlogResponse> blogResponsePage = blogService.getAllActive(page - 1)
                .map(BlogMapper::convertToBlogResponse);
        model.addAttribute("blogList", blogResponsePage.getContent());
        model.addAttribute("current_page", page);
        model.addAttribute("total_page", blogResponsePage.getTotalPages());
        return "blog";
    }

    @GetMapping("/blog-detail/{id}")
    public String blogDetail(Model model, @PathVariable(value = "id") long blogId) throws Throwable {
        BlogResponse blogResponse = BlogMapper.convertToBlogResponse(blogService.getDetail(blogId));
        model.addAttribute("blogResponse", blogResponse);
        return "blog_detail";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        return "faq";
    }

    @GetMapping("/term-and-condition")
    public String termAndCondition(Model model) {
        return "term_and_condition";
    }
}

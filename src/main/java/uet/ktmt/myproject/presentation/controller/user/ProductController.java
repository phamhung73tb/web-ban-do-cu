package uet.ktmt.myproject.presentation.controller.user;

import uet.ktmt.myproject.presentation.mapper.*;
import uet.ktmt.myproject.presentation.request.ProductPropertyRequest;
import uet.ktmt.myproject.presentation.request.ProductRequest;
import uet.ktmt.myproject.presentation.response.DeliveryAddressResponse;
import uet.ktmt.myproject.presentation.response.OrderResponse;
import uet.ktmt.myproject.presentation.response.ProductResponse;
import uet.ktmt.myproject.presentation.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uet.ktmt.myproject.service.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ImageProductService imageProductService;
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    static final String UPLOAD_DIR_IMAGE_PRODUCT = "src/main/resources/static/product/";

    @GetMapping("/create-product")
    public String getPageCreateProduct() {
        return "user/form_create_product";
    }

    @GetMapping("/get-form/{id}")
    public String getForm(Model model, @PathVariable(value = "id") long categoryId) throws Throwable {
        List<ProductPropertyRequest> propertyRequestList = categoryService.getListProperty(categoryId)
                .stream()
                .map(PropertyMapper::convertToPropertyRequest)
                .collect(Collectors.toList());

        ProductRequest productRequest = ProductRequest.builder().productPropertyRequestList(propertyRequestList).build();
        model.addAttribute("productRequest", productRequest);
        return "user/form_create_product_detail";
    }

    @PostMapping("/create")
    public ResponseEntity<Long> editAvatar(@RequestBody ProductRequest productRequest) throws Throwable {
        long productId = productService.create(ProductMapper.convertToProduct(productRequest)).getId();
        return ResponseEntity.ok().body(productId);
    }

    @PostMapping("/upload-image-product/{id}")
    public ResponseEntity<String> uploadImage(@PathVariable(value = "id") long productId
            , @RequestPart(value = "images") MultipartFile[] multipartFiles) throws Throwable {
        productService.uploadImage(productId, multipartFiles);
        return ResponseEntity.ok().body("Cập nhật ảnh thành công !!!");
    }

    @GetMapping("/detail/{id}")
    public String getDetailProduct(Model model, @PathVariable(value = "id") long productId) throws Throwable {
        ProductResponse productResponse = ProductMapper.convertToProductResponse(productService.getMyDetailProduct(productId));
        model.addAttribute("productResponse", productResponse);
        return "user/my_product_detail";
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<String> editProduct(@PathVariable(value = "id") long productId
            , @RequestBody ProductRequest productRequest) throws Throwable {
        productRequest.setId(productId);
        productService.edit(ProductMapper.convertToProduct(productRequest));
        return ResponseEntity.ok().body("Sửa thành công !!!");
    }

    @DeleteMapping("/delete-image/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "id") long imageProductId) throws Throwable {
        imageProductService.deleteImage(imageProductId);
        return ResponseEntity.ok().body("Xóa thành công !!!");
    }

    @PostMapping("/add-image/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable(value = "id") long productId
            , @RequestPart(value = "image") MultipartFile multipartFile) throws Throwable {
        imageProductService.addImage(productId, multipartFile);
        return ResponseEntity.ok().body("Thêm ảnh thành công !!!");
    }

    @PatchMapping("/change-main-image/{id}/{imageId}")
    public ResponseEntity<String> changeMainImage(@PathVariable(value = "id") long productId
            , @PathVariable(value = "imageId") long imageProductId) throws Throwable {
        imageProductService.changeMainImage(productId, imageProductId);
        return ResponseEntity.ok().body("Sửa ảnh bìa ảnh thành công !!!");
    }

    @PatchMapping("/sold-out/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable(value = "id") long productId) throws Throwable {
        productService.updateStatus(productId);
        return ResponseEntity.ok().body("Cập nhật thành công !!!");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") long productId) throws Throwable {
        productService.delete(productId);
        return ResponseEntity.ok().body("Xóa thành công !!!");
    }

    @GetMapping("/get-info-delivery/{id}")
    public ResponseEntity<DeliveryAddressResponse> getInfoDelivery(@PathVariable(value = "id") long productId) throws Throwable {

        DeliveryAddressResponse deliveryAddressResponse = DeliveryAddressMapper.convertToDeliveryAddressResponse(
                deliveryAddressService.getInfoDelivery(productId)
        );
        return ResponseEntity.ok().body(deliveryAddressResponse);
    }

    @PostMapping("/delivery-confirmation/{id}")
    public ResponseEntity<String> deliveryConfirmation(@PathVariable(value = "id") long productId) throws Throwable {

        OrderResponse orderResponse = OrderMapper.convertToOrderResponse(productService.deliveryConfirmation(productId));
        String content = "<body><h4>Đơn hàng trên Chợ cũ của bạn đã được xác nhận và chờ giao. Cảm ơn bạn đã tin dùng Chợ cũ</h4>"
                + "<table width='100%' border='1' cellspacing='0' cellpadding='10px'>"
                + "<tr><th>Mã đơn hàng</th><td>" + orderResponse.getId()
                + "</td></tr><tr><th>Ngày đặt</th><td>" + orderResponse.getCreateTime()
                + "</td></tr><tr><th>Sản phẩm</th><td>" + orderResponse.getProductResponse().getName()
                + "</td></tr><tr><th>Tổng tiền thanh toán</th><td>" + (orderResponse.getPriceProduct() + orderResponse.getFeeShipping())
                + "</td></tr><tr><th>Phương thức thanh toán</th><td>" + orderResponse.getMethodPayment()
                + "</td></tr><tr><th>Người nhận</th><td>" + orderResponse.getDeliveryAddressResponse().getFullName()
                + "</td></tr><tr><th>Số điện thoại</th><td>" + orderResponse.getDeliveryAddressResponse().getCellphone()
                + "</td></tr><tr><th>Địa chỉ</th><td>" + orderResponse.getDeliveryAddressResponse().getDetail()
                    + ", " + orderResponse.getDeliveryAddressResponse().getCommune()
                    + ", " + orderResponse.getDeliveryAddressResponse().getDistrict()
                    + ", " + orderResponse.getDeliveryAddressResponse().getProvince() + "</td></tr>"
                + "</table></body>";
        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getUserByUsername(orderResponse.getCreateBy()));
        //emailService.send(userResponse.getEmail(), "Chợ cũ - Đơn hàng của bạn đã được xác nhận", content);
        return ResponseEntity.ok().body("Xác nhận thành công !!!");
    }

    @PostMapping("/cancel-delivery/{id}")
    public ResponseEntity<String> cancelDelivery(@PathVariable(value = "id") long productId) throws Throwable {

        productService.cancelDelivery(productId);
        return ResponseEntity.ok().body("Đã hủy !!!");
    }
}

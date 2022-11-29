package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.common.myEnum.ProductStatusEnum;
import uet.ktmt.myproject.persistance.entity.Category;
import uet.ktmt.myproject.persistance.entity.ImageProduct;
import uet.ktmt.myproject.persistance.entity.Product;
import uet.ktmt.myproject.presentation.controller.basic.ImageController;
import uet.ktmt.myproject.presentation.request.ProductRequest;
import uet.ktmt.myproject.presentation.response.ProductResponse;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ProductMapper {
    private ProductMapper() {
        super();
    }

    public static ProductResponse convertToProductResponse(Product product) {
        String fileName = product.getImageProducts()
                .stream()
                .filter(i -> i.isMainImage())
                .findFirst()
                .orElse(ImageProduct.builder().fileName("not_found.png").build())
                .getFileName();
        String apiImage = MvcUriComponentsBuilder.fromMethodName(ImageController.class, "readDetailFile"
                , "product", product.getId().toString(), fileName).toUriString();
        String status = "Đang bán";
        if (product.getStatus().equals(ProductStatusEnum.STOCKING)) {
            status = "Đang bán";
        }
        if (product.getStatus().equals(ProductStatusEnum.SOLD_OUT)) {
            status = "Đã bán";
        }
        if (product.getStatus().equals(ProductStatusEnum.WAITING_CONFIRM)) {
            status = "Chờ xác nhận";
        }
        if (product.getStatus().equals(ProductStatusEnum.COMPLETED)) {
            status = "Đã thanh toán cho người bán";
        }

        String tag = "";
        if (LocalDateTime.now().minusDays(1).isBefore(product.getCreatedDate())) {
            tag = "New";
        }
        if (product.getView() > 10) {
            tag = "Hot";
        }
        if (product.getStatus().equals(ProductStatusEnum.SOLD_OUT)) {
            tag = "Đã bán";
        }

        return ProductResponse.builder()
                .id(product.getId())
                .slug(product.getSlug())
                .name(product.getName())
                .status(status)
                .view(product.getView())
                .price(product.getPrice())
                .currentStatus(product.getCurrentStatus())
                .description(product.getDescription())
                .mainImage(apiImage)
                .propertyResponseList(product.getProductProperties()
                        .stream()
                        .map(ProductPropertyMapper::convertToProductPropertyResponse)
                        .collect(Collectors.toList()))
                .apiGetImageList(product.getImageProducts()
                        .stream()
                        .map(ImageProductMapper::convertToImageProductResponse)
                        .collect(Collectors.toList()))
                .addressResponse(AddressMapper.convertToAddressResponse(product.getAddress()))
                .categoryResponse(CategoryMapper.convertToCategoryResponse(product.getCategory()))
                .tag(tag)
                .sellerName(product.getUser().getFullName())
                .sellerPhone(product.getUser().getCellphone())
                .userId(product.getUser().getId())
                .build();
    }

    public static Product convertToProduct(ProductRequest productRequest) {
        return Product.builder()
                .id(productRequest.getId())
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .currentStatus(productRequest.getCurrentStatus())
                .description(productRequest.getDescription())
                .productProperties(productRequest.getProductPropertyRequestList()
                        .stream()
                        .map(ProductPropertyMapper::convertToProductProperty)
                        .collect(Collectors.toList()))
                .address(AddressMapper.convertToAddress(productRequest.getAddressRequest()))
                .category(Category.builder().id(productRequest.getCategoryId()).build())
                .build();
    }
}

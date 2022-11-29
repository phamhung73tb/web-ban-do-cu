package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.ImageProduct;
import uet.ktmt.myproject.presentation.controller.basic.ImageController;
import uet.ktmt.myproject.presentation.response.ImageProductResponse;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

public class ImageProductMapper {
    private ImageProductMapper() {
        super();
    }

    public static ImageProductResponse convertToImageProductResponse(ImageProduct imageProduct) {

        String apiImage = MvcUriComponentsBuilder.fromMethodName(ImageController.class, "readDetailFile"
                , "product", imageProduct.getProduct().getId().toString(), imageProduct.getFileName()).toUriString();

        return ImageProductResponse.builder()
                .id(imageProduct.getId())
                .apiGetImage(apiImage)
                .isMainImage(imageProduct.isMainImage())
                .build();
    }
}

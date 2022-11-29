package uet.ktmt.myproject.presentation.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private long id;
    private String slug;
    private String name;
    private long price;
    private String status;
    private long view;
    private String currentStatus;
    private String description;
    private String mainImage;
    private List<ProductPropertyResponse> propertyResponseList;
    private List<ImageProductResponse> apiGetImageList;
    private AddressResponse addressResponse;
    private CategoryResponse categoryResponse;

    private String tag;
    private String sellerName;
    private String sellerPhone;
    private long userId;
}

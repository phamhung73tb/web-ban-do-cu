package uet.ktmt.myproject.presentation.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private long id;
    private String name;
    private long price;
    private String currentStatus;
    private String description;
    private List<ProductPropertyRequest> productPropertyRequestList;
    private AddressRequest addressRequest;
    private long categoryId;
}

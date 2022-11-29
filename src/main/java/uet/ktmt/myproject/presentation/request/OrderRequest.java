package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private long id;
    private long feeShipping;
    private String methodPayment;
    private DeliveryAddressRequest deliveryAddressRequest;
    private long productId;
}

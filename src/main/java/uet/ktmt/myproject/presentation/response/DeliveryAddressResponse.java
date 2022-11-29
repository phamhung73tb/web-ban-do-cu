package uet.ktmt.myproject.presentation.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAddressResponse {
    private long id;
    private String province;
    private int codeProvince;
    private String district;
    private int codeDistrict;
    private String commune;
    private int codeCommune;
    private String detail;
    private String fullName;
    private String cellphone;
}

package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
    private String province;
    private int codeProvince;
    private String district;
    private int codeDistrict;
    private String commune;
    private int codeCommune;
    private String detail;
}

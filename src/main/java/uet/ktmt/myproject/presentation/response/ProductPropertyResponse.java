package uet.ktmt.myproject.presentation.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPropertyResponse {
    private long propertyId;
    private String propertyName;
    private String value;
    private String note;
    private String unit;
}

package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPropertyRequest {
    private long propertyId;
    private String name;
    private String note;
    private String value;
}

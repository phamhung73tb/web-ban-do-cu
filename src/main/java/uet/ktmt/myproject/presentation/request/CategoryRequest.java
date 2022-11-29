package uet.ktmt.myproject.presentation.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {
    private long id;
    private String name;
    private String slug;
    private long categoryParentId;
    private List<Long> propertyRequestList;
}

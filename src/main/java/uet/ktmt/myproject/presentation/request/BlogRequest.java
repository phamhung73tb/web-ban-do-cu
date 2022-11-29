package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequest {
    private long id;
    private String title;
    private String content;
    private boolean status;
}

package uet.ktmt.myproject.presentation.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponse {
    private long id;
    private String apiGetImage;
    private String title;
    private String content;
    private String dateTime;
    private boolean status;
}

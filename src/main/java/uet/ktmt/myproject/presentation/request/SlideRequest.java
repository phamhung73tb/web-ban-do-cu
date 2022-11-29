package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlideRequest {
    private long id;
    private String title;
    private String titleStrong;
    private String link;
    private boolean status;
}

package uet.ktmt.myproject.presentation.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private long id;
    private long roomId;
    private UserResponse userSend;
    private String value;
    private String createdDate;
}

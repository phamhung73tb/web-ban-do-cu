package uet.ktmt.myproject.presentation.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private String cellphone;
    private String fullName;
    private String apiGetAvatar;
    private String status;
}

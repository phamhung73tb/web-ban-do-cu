package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private long id;
    private String username;
    private String fullName;
    private String email;
    private String cellphone;
    private String password;
    private boolean status;// 0: bình thường 1: tạm khóa
    private boolean resetPassword;
}

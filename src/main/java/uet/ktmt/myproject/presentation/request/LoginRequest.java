package uet.ktmt.myproject.presentation.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @Getter(AccessLevel.NONE)
    private String username;
    private String password;
    public String getUsername() {
        if(username != null)
        {
            return username.toLowerCase();
        }
        return null;
    }
}

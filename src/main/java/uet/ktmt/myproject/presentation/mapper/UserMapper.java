package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.presentation.controller.basic.ImageController;
import uet.ktmt.myproject.presentation.request.UserRequest;
import uet.ktmt.myproject.presentation.response.UserResponse;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

public class UserMapper {
    private UserMapper() {
        super();
    }

    public static User convertToUser(UserRequest userRequest) {
        return User.builder()
                .id(userRequest.getId())
                .username(userRequest.getUsername() != null ? userRequest.getUsername().toLowerCase() : null)
                .password(userRequest.getPassword())
                .email(userRequest.getEmail().toLowerCase())
                .cellphone(userRequest.getCellphone())
                .fullName(userRequest.getFullName())
                .hiddenFlag(userRequest.isStatus())
                .build();
    }

    public static UserResponse convertToUserResponse(User user) {
        String apiGetAvatar = MvcUriComponentsBuilder.fromMethodName(ImageController.class, "readDetailFile"
                , user.getClass().getSimpleName().toLowerCase(), user.getId().toString(), user.getAvatar()).toUriString();
        if (user.getAvatar() == null || user.getAvatar().equals("avatar_default.png")) {
            apiGetAvatar = MvcUriComponentsBuilder.fromMethodName(ImageController.class, "readDetailFile"
                    , user.getClass().getSimpleName().toLowerCase(), "0", "avatar_default.png").toUriString();
        }

        String status = user.getHiddenFlag() ? "Tạm khóa" : "Hoạt động bình thường";

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .cellphone(user.getCellphone())
                .fullName(user.getFullName())
                .apiGetAvatar(apiGetAvatar)
                .status(status)
                .build();
    }
}

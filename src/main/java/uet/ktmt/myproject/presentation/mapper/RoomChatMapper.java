package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.RoomChat;
import uet.ktmt.myproject.presentation.response.RoomChatResponse;
import uet.ktmt.myproject.presentation.response.UserResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;

public class RoomChatMapper {
    private RoomChatMapper() {
        super();
    }

    public static RoomChatResponse convertToResponse(RoomChat roomChat) {
        UserResponse receiverResponse = null;
        if (roomChat.getUsers() != null) {
            receiverResponse = UserMapper.convertToUserResponse(
                    roomChat.getUsers()
                            .stream()
                            .filter(u -> !u.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                            .findFirst()
                            .orElse(null)
            );
        }
        return RoomChatResponse.builder()
                .roomId(roomChat.getId())
                .receiver(receiverResponse)
                .updatedDate(roomChat.getUpdatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .build();
    }
}

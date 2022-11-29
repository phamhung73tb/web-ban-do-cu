package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.Message;
import uet.ktmt.myproject.persistance.entity.RoomChat;
import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.presentation.request.MessageRequest;
import uet.ktmt.myproject.presentation.response.MessageResponse;

import java.time.format.DateTimeFormatter;

public class MessageMapper {
    private MessageMapper(){super();}

    public static MessageResponse convertToResponse(Message message){
        return MessageResponse.builder()
                .id(message.getId())
                .userSend(UserMapper.convertToUserResponse(message.getUserSend()))
                .value(message.getValue())
                .createdDate(message.getCreatedDate().format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")))
                .build();
    }

    public static Message convertToEntity(MessageRequest messageRequest){
        return Message.builder()
                .roomChat(RoomChat.builder().id(messageRequest.getRoomId()).build())
                .userSend(User.builder().username(messageRequest.getUsername()).build())
                .value(messageRequest.getValue())
                .build();
    }
}

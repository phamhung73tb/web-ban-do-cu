package uet.ktmt.myproject.presentation.controller;

import uet.ktmt.myproject.presentation.mapper.MessageMapper;
import uet.ktmt.myproject.presentation.mapper.RoomChatMapper;
import uet.ktmt.myproject.presentation.mapper.UserMapper;
import uet.ktmt.myproject.presentation.response.MessageListResponse;
import uet.ktmt.myproject.presentation.response.MessageResponse;
import uet.ktmt.myproject.presentation.response.RoomChatResponse;
import uet.ktmt.myproject.presentation.response.UserResponse;
import uet.ktmt.myproject.service.RoomChatService;
import uet.ktmt.myproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/room-chat")
public class RoomChatController {
    @Autowired
    private RoomChatService roomChatService;
    @Autowired
    private UserService userService;

    @GetMapping("/room")
    public String getRoom(Model model, @RequestParam(value = "id", required = false, defaultValue = "-1") long sendUserId) throws Throwable {
        if (sendUserId != -1) {
            RoomChatResponse roomChatResponse = RoomChatMapper.convertToResponse(roomChatService.getRoom(sendUserId));
            UserResponse receiver = UserMapper.convertToUserResponse(userService.getUserById(sendUserId));
            roomChatResponse.setReceiver(receiver);
            model.addAttribute("roomChatResponse", roomChatResponse);
        }
        model.addAttribute("myUserId", userService.getCurrentUser().getId());
        return "user/room_chat";
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListRoom() throws Throwable {
        List<RoomChatResponse> roomChatResponses = roomChatService.findAllOfUser()
                .stream()
                .map(RoomChatMapper::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(roomChatResponses);
    }

//    @GetMapping("/get-room-by-user_id")
//    public ResponseEntity getRoomInfoByUserId(@Param(value = "id") long toUserId) throws Throwable {
//        long roomId = roomChatService.getRoomId(toUserId);
//        UserResponse userResponse = UserMapper.convertToUserResponse(userService.getUserById(toUserId));
//
//        return ResponseEntity.ok().body(new RoomChatResponse(roomId, userResponse));
//    }

    @GetMapping("/get-room-by-room_id")
    public ResponseEntity<?> getRoomInfoByRoomId(@RequestParam(value = "id") long roomId) throws Throwable {
        RoomChatResponse roomChatResponse = RoomChatMapper.convertToResponse(roomChatService.findById(roomId));
        UserResponse userResponse = UserMapper.convertToUserResponse(roomChatService.getReceiver(roomId));
        roomChatResponse.setReceiver(userResponse);
        return ResponseEntity.ok().body(roomChatResponse);
    }

    @GetMapping("/get-message/{id}")
    public ResponseEntity<?> getMessage(@PathVariable(value = "id") long roomId
            , @RequestParam(value = "currentMessage", required = false, defaultValue = "0") long currentMessage
            , @RequestParam(value = "oldTotalMessage", required = false, defaultValue = "0") long oldTotalMessage) throws Throwable {
        List<MessageResponse> messageResponses = roomChatService.getMessage(roomId, currentMessage, oldTotalMessage)
                .stream()
                .map(MessageMapper::convertToResponse)
                .collect(Collectors.toList());
        long newTotalMessage = roomChatService.getTotalMessageByRoomId(roomId);
        if(currentMessage != 0){
            currentMessage = currentMessage + (newTotalMessage - oldTotalMessage) + 20;
        }else {
            currentMessage = 20;
        }

        MessageListResponse messageListResponse = new MessageListResponse();
        messageListResponse.setMessageResponseList(messageResponses);
        messageListResponse.setCurrentMessage(currentMessage);
        messageListResponse.setOldTotalMessage(newTotalMessage);

        return ResponseEntity.ok().body(messageListResponse);
    }
}

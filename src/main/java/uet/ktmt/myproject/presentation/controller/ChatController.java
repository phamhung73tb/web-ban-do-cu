package uet.ktmt.myproject.presentation.controller;

import uet.ktmt.myproject.presentation.mapper.MessageMapper;
import uet.ktmt.myproject.presentation.request.MessageRequest;
import uet.ktmt.myproject.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private MessageService messageService;

    @MessageMapping("/server")
    public void send(SimpMessageHeaderAccessor sha, @Payload MessageRequest messageRequest) throws Throwable {
        messageService.saveMessage(MessageMapper.convertToEntity(messageRequest));
        simpMessagingTemplate.convertAndSendToUser(
                messageRequest.getUsername()
                , "/topic/" + messageRequest.getRoomId()
                , messageRequest.getValue());
    }
}

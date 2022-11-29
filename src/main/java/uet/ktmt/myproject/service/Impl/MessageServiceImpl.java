package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.persistance.entity.Message;
import uet.ktmt.myproject.persistance.entity.RoomChat;
import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.persistance.repository.MessageRepository;
import uet.ktmt.myproject.persistance.repository.RoomChatRepository;
import uet.ktmt.myproject.persistance.repository.UserRepository;
import uet.ktmt.myproject.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
public class MessageServiceImpl implements MessageService {
    @Autowired
    private RoomChatRepository roomChatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public void saveMessage(Message convertToEntity) throws Throwable{
        RoomChat foundRoomChat = roomChatRepository.findById(convertToEntity.getRoomChat().getId()).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy nhóm chat !!!");
                }
        );

        User foundUser = userRepository.findByUsername(convertToEntity.getUserSend().getUsername()).orElseThrow(() -> {
                    throw new BadRequestException("Không tìm thấy user !!!");
                }
        );
        convertToEntity.setUserSend(foundUser);
        convertToEntity.setRoomChat(foundRoomChat);
        convertToEntity.getRoomChat().setUpdatedDate(LocalDateTime.now());
        messageRepository.save(convertToEntity);
    }
}

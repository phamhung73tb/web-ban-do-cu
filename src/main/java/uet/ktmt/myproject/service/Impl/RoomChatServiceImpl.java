package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.common.exception.BadRequestReturnPageException;
import uet.ktmt.myproject.persistance.entity.Message;
import uet.ktmt.myproject.persistance.entity.RoomChat;
import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.persistance.repository.MessageRepository;
import uet.ktmt.myproject.persistance.repository.RoomChatRepository;
import uet.ktmt.myproject.persistance.repository.UserRepository;
import uet.ktmt.myproject.service.RoomChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoomChatServiceImpl implements RoomChatService {
    @Autowired
    private RoomChatRepository roomChatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;

    public RoomChat getRoom(long toUserId) throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy user !!!");
                }
        );
        if (foundUser.getId() == toUserId) {
            throw new BadRequestReturnPageException("Không thể chat với chính mình !!!");
        }

        User foundUserSend = userRepository.findById(toUserId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy user !!!");
                }
        );

        RoomChat foundRoomChat = roomChatRepository.findRoomOf(foundUser.getId(), toUserId).orElse(null);
        if (foundRoomChat != null) {
            return foundRoomChat;
        } else {
            RoomChat newRomChat = new RoomChat();
            List<User> listUser = new ArrayList<>();
            listUser.add(foundUser);
            listUser.add(foundUserSend);
            newRomChat.setUsers(listUser);
            return roomChatRepository.save(newRomChat);
        }
    }

    public List<Message> getMessage(long roomId, long currentMessage, long oldTotalMessage) {
        if (currentMessage != 0 && currentMessage >= oldTotalMessage) {
            throw new BadRequestException("Không còn tin nhắn !!!");
        }
        List<Message> result = new ArrayList<>();
        if (currentMessage == 0) {
            result = messageRepository.getListMessage(roomId, 20);
        } else {
            long newTotalMessage = messageRepository.totalMessageByRoomId(roomId);
            currentMessage = newTotalMessage - oldTotalMessage + currentMessage;
            result = messageRepository.getListMessageFrom(roomId, currentMessage, 20);
        }
        //Collections.reverse(result);
        return result;
    }

    public long getTotalMessageByRoomId(long roomId) {
        return messageRepository.totalMessageByRoomId(roomId);
    }

    public User getReceiver(long roomId) throws Throwable {
        RoomChat foundRoomChat = roomChatRepository.findById(roomId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy kênh chat !!!");
                }
        );
        return foundRoomChat.getUsers()
                .stream()
                .filter(u -> !u.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst()
                .orElseThrow(
                        () -> {
                            throw new BadRequestException("Không tìm thấy người chat !!!");
                        }
                );
    }

    public List<RoomChat> findAllOfUser() throws Throwable {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy user !!!");
                }
        );
        return roomChatRepository.findByUserId(foundUser.getId());
    }

    public RoomChat findById(long roomId) throws Throwable {
        return roomChatRepository.findById(roomId).orElseThrow(
                () -> {
                    throw new BadRequestException("Không tìm thấy kênh chat !!!");
                }
        );
    }
}

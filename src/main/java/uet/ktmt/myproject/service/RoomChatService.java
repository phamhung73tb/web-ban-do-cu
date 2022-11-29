package uet.ktmt.myproject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uet.ktmt.myproject.persistance.entity.Message;
import uet.ktmt.myproject.persistance.entity.RoomChat;
import uet.ktmt.myproject.persistance.entity.User;

@Service
public interface RoomChatService {
    public RoomChat getRoom(long toUserId) throws Throwable;

    public List<Message> getMessage(long roomId, long currentMessage, long oldTotalMessage) ;

    public long getTotalMessageByRoomId(long roomId) ;

    public User getReceiver(long roomId) throws Throwable;

    public List<RoomChat> findAllOfUser() throws Throwable ;

    public RoomChat findById(long roomId) throws Throwable ;
}

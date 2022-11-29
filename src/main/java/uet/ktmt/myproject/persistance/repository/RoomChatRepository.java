package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.RoomChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomChatRepository extends JpaRepository<RoomChat, Long> {
    @Query(value = "select * from room_chat inner join user_rooms on room_chat.id = user_rooms.room_id " +
            "where user_rooms.user_id = :user1 " +
            "and user_rooms.room_id in (select room_id from user_rooms where user_rooms.user_id = :user2)"
            , nativeQuery = true)
    Optional<RoomChat> findRoomOf(@Param(value= "user1") long user1, @Param(value= "user2") long user2);
    @Query(value = "select * from room_chat inner join user_rooms on room_chat.id = user_rooms.room_id " +
            "where user_rooms.user_id = :userId " +
            "order by updated_date desc"
            , nativeQuery = true)
    List<RoomChat> findByUserId(@Param(value= "userId") long userId);
}

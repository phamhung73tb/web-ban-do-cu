package uet.ktmt.myproject.persistance.repository;

import uet.ktmt.myproject.persistance.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "select * from message " +
            "where room_id = :roomId " +
            "order by id desc " +
            "limit :start, :limit"
            , nativeQuery = true)
    List<Message> getListMessageFrom(@Param(value = "roomId") long roomId
            , @Param(value = "start") long start
            , @Param(value = "limit") int limit);
    @Query(value = "select * from message " +
            "where room_id = :roomId " +
            "order by id desc " +
            "limit :limit"
            , nativeQuery = true)
    List<Message> getListMessage(@Param(value = "roomId") long roomId
            , @Param(value = "limit") int limit);
    @Query(value = "select count(*) from message " +
            "where room_id = :roomId "
            , nativeQuery = true)
    Long totalMessageByRoomId(@Param(value = "roomId") long roomId);

}

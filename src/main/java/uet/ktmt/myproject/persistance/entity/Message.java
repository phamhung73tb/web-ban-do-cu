package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Message extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomChat roomChat;
    @ManyToOne
    @JoinColumn(name = "user_send")
    private User userSend;
    private String value;
}

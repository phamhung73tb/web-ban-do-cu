package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 10, unique = true)
    private String cellphone;

    @Column(name ="full_name", length = 50)
    private String fullName;

    @Column(name = "avatar")
    private String avatar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user")
    private Collection<Product> products;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "wishlist", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Collection<Product> wishlistProducts;

    @OneToMany(mappedBy = "user")
    private Collection<Order> orderedList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_rooms", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "room_id"))
    private Collection<RoomChat> roomChats;

    @OneToMany(mappedBy = "userSend")
    private Collection<Message> messages;

    @OneToMany(mappedBy = "user")
    private Collection<HistoryClick> historyClick;
}

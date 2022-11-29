package uet.ktmt.myproject.persistance.entity;

import uet.ktmt.myproject.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Otp extends BaseEntity {

    @Column(name = "otp_pass", length = 4)
    private String otpPass; // giá trị otp

    @Column(name = "expired_time")
    private Timestamp expiredTime; // thời gian hết hạn

    @Column(name = "fail")
    private int fail; // số lần sai

    @Column(nullable = false, unique = true, name = "cellphone")
    private String cellphone; // số nhận otp

    @Column(name = "status", length = 10)
    private String status; //trạng thái
}

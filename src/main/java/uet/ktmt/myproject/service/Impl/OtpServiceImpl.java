package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.persistance.entity.Otp;
import uet.ktmt.myproject.persistance.repository.OtpRepository;
import uet.ktmt.myproject.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class OtpServiceImpl implements OtpService {
    @Autowired
    OtpRepository otpRepository;

    private static final int MAX_FAIL = 5;
    private static final int TIME_WAIT = 5; // MINUTES
    private static final int EXPIRED_TIME_OTP = 5; // MINUTES
    private static final String STATUS_VERIFYING = "Verifying";

    @Transactional
    public void addOtp(String otp, String cellphone) {
        Optional<Otp> foundOtp = otpRepository.findByCellphone(cellphone);

        //check đã tạo otp lần nảo trên hệ thống hay chưa
        if (foundOtp.isPresent()) {
            //check vượt quá số lần gửi cho phép
            if (foundOtp.get().getFail() > MAX_FAIL) {
                //check xem hết thời gian đợi hay chưa
                if (foundOtp.get().getExpiredTime()
                        .after(Timestamp.valueOf(LocalDateTime.now().minus(TIME_WAIT, ChronoUnit.MINUTES)))) {
                    throw new SecurityException("Đã vượt quá số lần cho phép, xin vui lòng đợi " + TIME_WAIT + " phút !!!");
                } else {
                    // reset số lần đã xác thực otp lỗi + tạo otp mới
                    foundOtp.get().setFail(0);
                    foundOtp.get().setOtpPass(otp);
                    foundOtp.get().setStatus(STATUS_VERIFYING);
                    foundOtp.get().setExpiredTime(Timestamp.valueOf(LocalDateTime.now().plus(EXPIRED_TIME_OTP, ChronoUnit.MINUTES)));
                    otpRepository.save(foundOtp.get());
                }
            }
            // set otp mới
            foundOtp.get().setOtpPass(otp);
            foundOtp.get().setStatus(STATUS_VERIFYING);
            foundOtp.get().setExpiredTime(Timestamp.valueOf(LocalDateTime.now().plus(EXPIRED_TIME_OTP, ChronoUnit.MINUTES)));
            otpRepository.save(foundOtp.get());
        } else {
            // tạo mới otp trong bảng otp
            Otp newOtp = new Otp();
            newOtp.setCellphone(cellphone);
            newOtp.setOtpPass(otp);
            newOtp.setStatus(STATUS_VERIFYING);
            newOtp.setFail(0);
            newOtp.setExpiredTime(Timestamp.valueOf(LocalDateTime.now().plus(EXPIRED_TIME_OTP, ChronoUnit.MINUTES)));
            otpRepository.save(newOtp);
        }
    }

    public String checkOtp(String otp, String cellphone) {
        Optional<Otp> foundOtp = otpRepository.findByCellphone(cellphone);
        if (foundOtp.isPresent()) {
            foundOtp.get().setFail(foundOtp.get().getFail() + 1);
            if (foundOtp.get().getOtpPass().equals(otp)
                    && foundOtp.get().getStatus().equals(STATUS_VERIFYING)
                    && foundOtp.get().getFail() <= MAX_FAIL
                    && foundOtp.get().getExpiredTime().after(Timestamp.valueOf(LocalDateTime.now()))) {
                foundOtp.get().setStatus("Verified");
                foundOtp.get().setFail(0);
                otpRepository.save(foundOtp.get());
                return "true";
            }
            otpRepository.save(foundOtp.get());
            return "Không đúng OTP, xin vui lòng thử lại !!!";
        }
        return "OTP không tồn tại. Xin hãy vui lòng gửi lại !!!";
    }

}

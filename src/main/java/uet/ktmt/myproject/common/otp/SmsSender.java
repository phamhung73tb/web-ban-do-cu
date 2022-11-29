package uet.ktmt.myproject.common.otp;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import uet.ktmt.myproject.common.exception.SmsSendException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsSender {
    private SmsSender() {
        super();
    }

    // Find your Account SID and Auth Token at twilio.com/console
    // and set the environment variables. See http://twil.io/secure
    //TWILIO_ACCOUNT_SID va TWILIO_AUTH_TOKEN lấy từ biến môi trường
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String HOST_PHONE = System.getenv("TWILIO_HOST_PHONE"); // +18565531074

    public static void sendOtp(String phoneNumber, String otp) {
        log.info("Mapped sendOtp method");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        try {
            Message message = Message.creator(
                            new com.twilio.type.PhoneNumber("+84" + phoneNumber),
                            new com.twilio.type.PhoneNumber(HOST_PHONE),
                            "Mã xác thực của bạn là: " + otp)
                    .create();
            log.info(message.getSid());
        } catch (Exception e) {
            throw new SmsSendException(e.getMessage());
        }
    }
}

package uet.ktmt.myproject.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    void addOtp(String otp, String cellphone);
    
    String checkOtp(String otp, String cellphone);
}

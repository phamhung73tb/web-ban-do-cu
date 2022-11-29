package uet.ktmt.myproject.service;

import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public interface EmailService {
    void send(String to, String subject, String text) throws MessagingException;
}

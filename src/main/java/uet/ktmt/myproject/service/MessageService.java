package uet.ktmt.myproject.service;

import org.springframework.stereotype.Service;

import uet.ktmt.myproject.persistance.entity.Message;

@Service
public interface MessageService {
    void saveMessage(Message convertToEntity) throws Throwable;
}

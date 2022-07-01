package com.github.domain.service;

import com.github.domain.model.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public Message deliverMessage(Message message) {
        System.out.println(message.getText());
        return message;
    }
}
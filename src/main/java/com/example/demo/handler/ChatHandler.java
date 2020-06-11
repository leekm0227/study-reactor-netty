package com.example.demo.handler;

import com.example.demo.fb.Chat;
import com.example.demo.fb.Payload;
import com.example.demo.publisher.ChatPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChatHandler extends AbstractHandler<Chat> {

    @Autowired
    ChatPublisher chatPublisher;

    @PostConstruct
    public void init() {
        clazz = Chat.class;
        demoHandlerFactory.register(Payload.Chat, this);
    }

    @Override
    public byte[] handle(Chat chat) {
        chatPublisher.onNext(chat);
        return chat.getByteBuffer().array();
    }
}

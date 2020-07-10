package com.example.demo.handler;

import com.example.demo.flatbuffer.FbChat;
import com.example.demo.flatbuffer.FbPayload;
import com.example.demo.publisher.ChatPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ChatHandler extends AbstractHandler<FbChat> {

    @Autowired
    ChatPublisher chatPublisher;

    @PostConstruct
    public void init() {
        cls = FbChat.class;
        demoHandlerFactory.register(FbPayload.FbChat, this);
    }

    @Override
    public byte[] handle(FbChat chat, byte method) {
        chatPublisher.onNext(chat);
        return empty();
    }
}

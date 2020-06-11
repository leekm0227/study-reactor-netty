package com.example.demo.publisher;

import com.example.demo.fb.Chat;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;

@Component
public class ChatPublisher {

    private UnicastProcessor<Chat> chatPublisher;
    private Flux<Chat> chatFlux;

    @PostConstruct
    public void init() {
        chatPublisher = UnicastProcessor.create();
        chatFlux = chatPublisher.replay(1).autoConnect(0);
    }

    public void onNext(Chat chat) {
        chatPublisher.onNext(chat);
    }

    public Flux<byte[]> subscribe() {
        return chatFlux.map(chat -> chat.getByteBuffer().array());
    }

}

package com.example.demo.publisher;

import com.example.demo.flatbuffer.FbChat;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;

@Component
public class ChatPublisher {

    private UnicastProcessor<FbChat> chatPublisher;
    private Flux<FbChat> chatFlux;

    @PostConstruct
    public void init() {
        chatPublisher = UnicastProcessor.create();
        chatFlux = chatPublisher.replay(1).autoConnect(0);
    }

    public void onNext(FbChat chat) {
        chatPublisher.onNext(chat);
    }

    public Flux<byte[]> subscribe() {
        return chatFlux.map(chat -> chat.getByteBuffer().array());
    }

}

package com.example.demo.publisher;

import com.example.demo.flatbuffer.FbChat;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class ChatPublisher {

    private UnicastProcessor<FbChat> chatPublisher;
    private Flux<FbChat> chatFlux;
    private HashMap<String, ArrayList<String>> topicMap;

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

    public Flux<byte[]> subscribe(String sid) {
        return chatFlux.map(chat -> topicMap.get(sid).stream().anyMatch(cid -> cid.equals(chat.cid())) ? chat.getByteBuffer().array() : new byte[0]);
    }

}

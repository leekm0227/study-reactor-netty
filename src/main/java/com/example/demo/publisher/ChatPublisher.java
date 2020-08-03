package com.example.demo.publisher;

import com.example.demo.flatbuffer.FbChat;
import com.example.demo.util.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;

@Component
public class ChatPublisher {

    @Autowired
    ChannelManager channelManager;

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

    public Flux<byte[]> subscribe(String sid) {
        return chatFlux.map(chat -> channelManager.readable(sid, chat.cid()) ? chat.getByteBuffer().array() : new byte[0]);
    }
}

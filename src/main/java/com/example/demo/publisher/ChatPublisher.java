package com.example.demo.publisher;

import com.example.demo.flatbuffer.FbChat;
import com.example.demo.util.ChannelManager;
import com.example.demo.util.FbConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ChatPublisher {

    @Autowired
    ChannelManager channelManager;

    private AtomicInteger count = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(ChatPublisher.class);
    public UnicastProcessor<FbChat> chatPublisher;
    public Flux<FbChat> chatFlux;

    @PostConstruct
    public void init() {
        chatPublisher = UnicastProcessor.create();
        chatFlux = chatPublisher.replay(1).autoConnect(0);
    }

    public void onNext(FbChat chat) {
        chatPublisher.onNext(chat);
    }

    public Flux<byte[]> subscribe(int hash) {
        return chatFlux.map(chat -> channelManager.readable(hash, chat.cid()) ? chat.getByteBuffer().array() : FbConverter.toEmpty());
    }
}

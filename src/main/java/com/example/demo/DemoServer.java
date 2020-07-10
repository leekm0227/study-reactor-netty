package com.example.demo;

import com.example.demo.model.RequestBean;
import com.example.demo.publisher.ChatPublisher;
import com.example.demo.publisher.FieldPublisher;
import com.example.demo.util.FbDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;

@Component
@Profile("!test")
public class DemoServer implements ApplicationRunner {

    @Autowired
    Dispatcher dispatcher;

    @Autowired
    ChatPublisher chatPublisher;

    @Autowired
    FieldPublisher fieldPublisher;

    @Override
    public void run(ApplicationArguments args) {
        TcpServer.create()
                .port(9999)
                .doOnConnection(conn -> conn.addHandler(new FbDecoder()))
                .handle((inbound, outbound) -> outbound.sendByteArray(
                        inbound.receiveObject()
                                .ofType(RequestBean.class)
                                .log("request")
                                .map(request -> dispatcher.handle(request))
                                .mergeWith(chatPublisher.subscribe())
                                .mergeWith(fieldPublisher.subscribe())
                        )
                )
                .bindUntilJavaShutdown(Duration.ofSeconds(30), null);
    }
}


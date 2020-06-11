package com.example.demo;

import com.example.demo.model.Request;
import com.example.demo.publisher.ChatPublisher;
import com.example.demo.publisher.FieldPublisher;
import com.example.demo.util.FbDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;

@Component
public class DemoServer {

    @Autowired
    Dispatcher dispatcher;

    @Autowired
    ChatPublisher chatPublisher;

    @Autowired
    FieldPublisher mapPublisher;

    void run() {
        TcpServer.create()
                .port(9999)
                .doOnConnection(conn -> conn.addHandler(new FbDecoder()))
                .handle((inbound, outbound) -> outbound.sendByteArray(
                        inbound.receiveObject()
                                .ofType(Request.class)
                                .log("request")
                                .map(request -> dispatcher.handle(request))
                                .mergeWith(chatPublisher.subscribe())
                                .mergeWith(mapPublisher.subscribe())
                        )
                )
                .bindUntilJavaShutdown(Duration.ofSeconds(30), null);
    }
}


package com.example.demo;

import com.example.demo.model.RequestBean;
import com.example.demo.util.FbDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;

@Component
@Profile("!api")
public class DemoServer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoServer.class);

    @Autowired
    Dispatcher dispatcher;

    @Override
    public void run(ApplicationArguments args) {
        TcpServer.create()
                .port(9999)
                .doOnConnection(conn -> {
                    logger.info("conn channel id : {}", conn.channel().id());
                    conn.addHandler(new FbDecoder());
                })
                .handle((inbound, outbound) -> outbound.sendByteArray(
                        inbound.receiveObject()
                                .ofType(RequestBean.class)
                                .log("request")
                                .doOnCancel(() -> logger.info("on cancel"))
                                .doOnError(throwable -> logger.info("on error : {}", throwable.getLocalizedMessage()))
                                .flatMap(request -> dispatcher.handle(request))
                        )
                )
                .bindUntilJavaShutdown(Duration.ofSeconds(30), null);
    }
}


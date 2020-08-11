package com.example.demo;

import com.example.demo.model.RequestBean;
import com.example.demo.publisher.ChatPublisher;
import com.example.demo.publisher.FieldPublisher;
import com.example.demo.util.ChannelManager;
import com.example.demo.util.FbDecoder;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("!api")
public class DemoServer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoServer.class);

    @Autowired
    Dispatcher dispatcher;

    @Autowired
    ChannelManager channelManager;

    @Autowired
    ChatPublisher chatPublisher;

    @Autowired
    FieldPublisher fieldPublisher;

    @Override
    public void run(ApplicationArguments args) {
        TcpServer.create()
                .option(ChannelOption.SO_LINGER, 0)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .port(9999)
                .doOnConnection(conn -> {
//                    channelManager.onConnect(conn);
                    conn.addHandler(new FbDecoder());
                })
                .handle((inbound, outbound) -> {
//                            inbound.receiveObject().ofType(RequestBean.class).
                            AtomicInteger count = new AtomicInteger(0);
                            inbound.receiveObject()
                                    .ofType(RequestBean.class)
                                    .subscribe(requestBean -> {
                                        logger.info("inbound count : {}", count.incrementAndGet());
                                        dispatcher.handle(requestBean);
                                    });

                            return outbound.sendByteArray(chatPublisher.subscribe(inbound.hashCode()));


//                            return outbound.sendByteArray(
//                                    inbound.receiveObject()
//                                            .ofType(RequestBean.class)
//                                .log("request")
//                                .doOnCancel(() -> channelManager.log())
//                                .doOnError(throwable -> logger.info("on error : {}", throwable.getLocalizedMessage()))
//                                            .map(requestBean -> {
//                                                logger.info("inbound count : {}", count.incrementAndGet());
//                                                return dispatcher.handle(requestBean);
//                                                return requestBean.getMessage().getByteBuffer().array();
//                                                return chatPublisher.chatFlux.map(fbChat -> fbChat.getByteBuffer().array());
//                                            }));
//                                            .mergeWith(chatPublisher.subscribe(inbound.hashCode())));
                        }
//                                .mergeWith(fieldPublisher.subscribe()))
                )
//                .wiretap(true)


//                .handle((inbound, outbound) -> {
//                    Flux<byte[]> result = inbound.receiveObject()
//                            .ofType(RequestBean.class)
//                            .map(requestBean -> dispatcher.handle(requestBean))
//                            .mergeWith(chatPublisher.subscribe(inbound.hashCode()));
//
//                    result.subscribe(bytes -> {
//                        logger.info("result size : {}", bytes.length);
//                    });
//                    return outbound.sendByteArray(result);
//                })
                .bindUntilJavaShutdown(Duration.ofSeconds(30), null);
    }
}


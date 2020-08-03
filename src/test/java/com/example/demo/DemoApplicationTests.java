package com.example.demo;


import com.example.demo.flatbuffer.FbChat;
import com.example.demo.flatbuffer.FbMessage;
import com.example.demo.util.FbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.util.AssertionErrors.assertTrue;


class DemoApplicationTests {

    @BeforeEach
    void before() {
    }

    @Test
    void testCid(){

    }











    @Test
    void testSocket() throws IOException, InterruptedException {
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9999);
        SocketChannel client = SocketChannel.open(hostAddress);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        int count = 0;
        while (count < 5) {
            System.out.println("write msg count : " + count);
            client.write(ByteBuffer.wrap(FbConverter.toChat("testcid", "testoid", "msg content" + count).getByteBuffer().array()));
            client.read(byteBuffer);

            byteBuffer.flip();
            FbMessage message = FbMessage.getRootAsFbMessage(byteBuffer);
            FbChat receiveChat = (FbChat) message.payload(new FbChat());

            if (receiveChat != null) System.out.println("receive msg : " + receiveChat.content());

            Thread.sleep(100);
            count++;
        }

        client.close();
    }

    @Test
    void testTcpClient() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        TcpClient client = TcpClient.create().port(9999);
        Connection conn = client.handle((in, out) -> {
            in.receive().asByteArray().log("client").subscribe(bytes -> {
                FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
                FbChat chat = (FbChat) message.payload(new FbChat());
                System.out.println("chat : " + chat.content());
                latch.countDown();
            });

            return out.sendByteArray(Flux.just(
                    FbConverter.toChat("testcid", "testoid", "msg content1").getByteBuffer().array(),
                    FbConverter.toChat("testcid", "testoid", "msg content2").getByteBuffer().array(),
                    FbConverter.toChat("testcid", "testoid", "msg content3").getByteBuffer().array(),
                    FbConverter.toChat("testcid", "testoid", "msg content4").getByteBuffer().array(),
                    FbConverter.toChat("testcid", "testoid", "msg content5").getByteBuffer().array()
            )).then();
        }).wiretap(true).connectNow();

        assertTrue("Latch was counted down", latch.await(5, TimeUnit.SECONDS));
        conn.disposeNow();
    }






}
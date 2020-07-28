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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.util.AssertionErrors.assertTrue;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@ContextConfiguration(classes = {DemoApplication.class, DemoServer.class}, initializers = ConfigFileApplicationContextInitializer.class)
class DemoApplicationTests {

//    @Autowired
//    ApplicationContext context;

    @BeforeEach
    void before() {
//        DemoServer server = context.getBean(DemoServer.class);
//        server.run(null);
    }

    @Test
    void test1() throws IOException, InterruptedException {
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9999);
        SocketChannel client = SocketChannel.open(hostAddress);

        int count = 0;
        while (count < 100) {
            System.out.println("write msg count : " + count);
            client.write(ByteBuffer.wrap(FbConverter.toChat("testcid", "testoid", "msg content" + count).getByteBuffer().array()));
            Thread.sleep(500);
            count++;
        }

        client.close();
    }


    @Test
    void test() throws InterruptedException {
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
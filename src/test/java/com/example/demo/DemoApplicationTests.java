package com.example.demo;


import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.FbField;
import com.example.demo.flatbuffer.FbMessage;
import com.example.demo.flatbuffer.FbPayload;
import com.example.demo.flatbuffer.FbState;
import com.example.demo.util.Const;
import com.example.demo.util.FbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


class DemoApplicationTests {

    final static String HOST = "127.0.0.1";
    final static int PORT = 9999;
    final AtomicInteger sendCount = new AtomicInteger(0);
    final AtomicInteger receiveCount = new AtomicInteger(0);
    AsynchronousChannelGroup channelGroup;

    @BeforeEach
    void before() throws IOException {
        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
    }

    @Test
    void testField() throws IOException, InterruptedException {
        Character character = Character.builder()
                .id("testId0001")
                .name("testName0001")
                .build();

        TestClient client = new TestClient(channelGroup, sendCount, buffer -> {
            FbMessage message = FbMessage.getRootAsFbMessage(buffer);

            if (message != null) {
                System.out.println("payload type : " + message.payloadType());
                if (message.payloadType() == FbPayload.FbField) {
                    FbField field = (FbField) message.payload(new FbField());
                    for (int i = 0; i < Objects.requireNonNull(field).objectsLength(); i++) {
                        System.out.println(String.format("name : %s, pos : [%2f, %2f]",
                                field.objects(i).name(),
                                field.objects(i).pos().x(),
                                field.objects(i).pos().y()
                        ));
                    }
                }
            }
        });

        client.connect();
        client.send(ByteBuffer.wrap(FbConverter.toAction(character, FbState.D)));
        Thread.sleep(1000);

        int i = 0;
        while (i < 5000) {
            Random random = new Random();
            int length = 10;
            int x = random.nextInt(length) - length / 2;
            int y = random.nextInt(length) - length / 2;
            character.setPos(Const.X, x);
            character.setPos(Const.Y, y);
            client.send(ByteBuffer.wrap(FbConverter.toAction(character, FbState.D)));
            Thread.sleep(50);
            i++;
        }

        channelGroup.shutdown();
    }


    @Test
    void testCid() throws IOException, InterruptedException {
        ArrayList<TestClient> testClients = testClients(10);
        testClients.forEach(TestClient::connect);

        int count = 0;
        while (count < 1000) {
            testClients.forEach(testClient -> {
                testClient.send(ByteBuffer.wrap(FbConverter.toChat(Const.TOPIC_NOTICE, "testoid", "msg content")));
            });
            Thread.sleep(50);
            count++;
        }

        Thread.sleep(50000);
        channelGroup.shutdown();
    }

    private ArrayList<TestClient> testClients(int size) throws IOException {
        ArrayList<TestClient> clients = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int finalI = i;
            clients.add(new TestClient(channelGroup, sendCount, buffer -> {
                FbMessage message = FbMessage.getRootAsFbMessage(buffer);

                if (message != null) {
                    System.out.println("===== receive" + finalI + " payload : " + FbPayload.name(message.payloadType()) + ", receive count : " + receiveCount.incrementAndGet());
                }
            }));
        }

        return clients;
    }

    private static class TestClient {
        private final AsynchronousSocketChannel socketChannel;
        private final ByteBuffer byteBuffer;
        private final Consumer<ByteBuffer> consumer;
        private final AtomicInteger sendCount;

        TestClient(AsynchronousChannelGroup channelGroup, AtomicInteger counter, Consumer<ByteBuffer> consumer) throws IOException {
            this.socketChannel = AsynchronousSocketChannel.open(channelGroup);
            this.byteBuffer = ByteBuffer.allocate(1024);
            this.consumer = consumer;
            this.sendCount = counter;
        }

        void connect() {
            socketChannel.connect(new InetSocketAddress(HOST, PORT), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    socketChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            consumer.accept(attachment);
                            byteBuffer.clear();

                            if (socketChannel.isOpen()) {
                                socketChannel.read(byteBuffer, byteBuffer, this);
                            }
                        }

                        @Override
                        public void failed(Throwable e, ByteBuffer attachment) {
                            close();
                        }
                    });
                }

                @Override
                public void failed(Throwable e, Void attachment) {
                    System.out.println("connect failed : " + e.getLocalizedMessage());
                }
            });
        }

        void send(ByteBuffer byteBuffer) {
            if (socketChannel.isOpen()) {
                socketChannel.write(byteBuffer, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer result, Void attachment) {
                        System.out.println("===== send size : " + byteBuffer.position() + ", send count : " + sendCount.incrementAndGet());
                    }

                    @Override
                    public void failed(Throwable e, Void attachment) {
                        System.out.println("send failed : " + e.getLocalizedMessage());
                    }
                });
            }
        }

        void close() {
            if (socketChannel.isOpen()) {
                try {
                    socketChannel.close();
                } catch (IOException ignored) {
                    // ignored
                }
            }
        }
    }
}
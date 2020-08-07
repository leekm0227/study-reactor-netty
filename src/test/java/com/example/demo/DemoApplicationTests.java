package com.example.demo;


import com.example.demo.domain.Account;
import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.*;
import com.example.demo.util.Const;
import com.example.demo.util.FbConverter;
import com.google.flatbuffers.FlatBufferBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


class DemoApplicationTests {

    final static String HOST = "127.0.0.1";
    final static int PORT = 9999;
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

        testClient client = new testClient(channelGroup, buffer -> {
            FbMessage message = FbMessage.getRootAsFbMessage(buffer);

            if (message != null) {
                System.out.println("payload type : " + message.payloadType());
                if (message.payloadType() == FbPayload.FbField) {
                    FbField field = (FbField) message.payload(new FbField());
                    for (int i = 0; i < Objects.requireNonNull(field).objectsLength(); i++) {
                        System.out.println(String.format("name : {}, pos : [{}, {}, {}]",
                                field.objects(i).name(),
                                field.objects(i).pos().x(),
                                field.objects(i).pos().y(),
                                field.objects(i).pos().z()
                        ));
                    }
                }
            }
        });

        client.connect();
        client.send(ByteBuffer.wrap(FbConverter.toAction(character, FbState.S).getByteBuffer().array()));
        Thread.sleep(100000);
        channelGroup.shutdown();
    }


    @Test
    void testCid() throws IOException, InterruptedException {
        testClient test1 = new testClient(channelGroup, buffer -> {
            FbMessage message = FbMessage.getRootAsFbMessage(buffer);

            if (message != null) {
                System.out.println("===== receive1 : " + message.payloadType());
            }
        });

        testClient test2 = new testClient(channelGroup, buffer -> {
            FbMessage message = FbMessage.getRootAsFbMessage(buffer);

            if (message != null) {
                System.out.println("===== receive2 : " + message.payloadType());
            }
        });

        test1.connect();
        test2.connect();

        int count = 0;
        while (count < 5) {
            test1.send(ByteBuffer.wrap(FbConverter.toChat(Const.TOPIC_NOTICE, "testoid", "msg content").getByteBuffer().array()));
            Thread.sleep(1000);
            count++;
        }

        channelGroup.shutdown();
    }

    private static class testClient {
        private final AsynchronousSocketChannel socketChannel;
        private final ByteBuffer byteBuffer;
        private final Consumer<ByteBuffer> consumer;

        testClient(AsynchronousChannelGroup channelGroup, Consumer<ByteBuffer> consumer) throws IOException {
            this.socketChannel = AsynchronousSocketChannel.open(channelGroup);
            this.byteBuffer = ByteBuffer.allocate(1024);
            this.consumer = consumer;
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
                        System.out.println("===== send size : " + byteBuffer.position());
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
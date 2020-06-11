package com.example.demo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoTask {

//    @Autowired
//    DemoSubscriber demoPublisher;

//    @Autowired
//    DemoChannelGroup demoChannelGroup;

    int i = 0;

    @Scheduled(fixedRate = 1000)
    public void task() {
//        demoChannelGroup.sendMessageOutbound("test");
//            demoPublisher.stringPublisher.onNext("test"+i);
//            i++;
//        Collection<Obj> objs = new ArrayList<>();
//        final int maxLength = 4;
//
//        Flux<Test> messageFlux = demoPublisher.getMessages();
//        messageFlux.last().flatMap(test -> {
//            test.
//
//
//            // filter cate : N
//            for (int i = 0; i < test.ObjsLength(); i++) {
//                Obj obj = test.Objs(i);
//
//                if (obj.cate() == Cate.N) {
//                    objs.add(obj);
//                }
//            }
//
//            if(objs.size() < maxLength){
//
//            }
//
//            return null;
//        });

//        FlatBufferBuilder builder = new FlatBufferBuilder(0);
//        int name = builder.createString("name");
//        Object.startObject(builder);
//        Object.addName(builder, name);
//        Object.addType(builder, Type.N);
//        Object.addState(builder, State.I);
//        Object.addPos(builder, Vec3.createVec3(builder, 0.0f, 0.1f, 0.2f));
//        int obj = Object.endObject(builder);
//        int test = Test.createTest(builder, obj);
//        builder.finish(Message.createMessage(builder, 1, Body.Test, test));
//        Message message = Message.getRootAsMessage(ByteBuffer.wrap(builder.sizedByteArray()));
//
//        demoPublisher.publish(message);
    }
}




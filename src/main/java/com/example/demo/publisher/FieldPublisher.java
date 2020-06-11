package com.example.demo.publisher;

import com.example.demo.fb.Field;
import com.google.flatbuffers.FlatBufferBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;

@Component
public class FieldPublisher {

    private UnicastProcessor<Field> fieldPublisher;
    private Flux<Field> fieldFlux;

    @PostConstruct
    public void init() {
        fieldPublisher = UnicastProcessor.create();
        fieldFlux = fieldPublisher.replay(1).autoConnect(0);
    }

    public void onNext(Field field) {
        fieldPublisher.onNext(field);
    }

    public Flux<byte[]> subscribe() {
        return fieldFlux.map(field -> {
            FlatBufferBuilder builder = new FlatBufferBuilder();


            for (int i = 0; i < field.objectsLength(); i++) {

            }

            builder.createByteVector(field.getByteBuffer());

//            int response = Map.createMap(builder, objects);
//            int message = Message.createMessage(builder, 0, Body.Map, response);
//            builder.finish(message);
            return builder.sizedByteArray();
        });
    }

}

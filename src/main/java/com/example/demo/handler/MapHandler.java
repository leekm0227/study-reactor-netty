package com.example.demo.handler;

import com.example.demo.fb.Field;
import com.example.demo.fb.Payload;
import com.example.demo.publisher.FieldPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MapHandler extends AbstractHandler<Field> {

    @Autowired
    FieldPublisher mapPublisher;

    @PostConstruct
    public void init() {
        demoHandlerFactory.register(Payload.Field, this);
    }

    @Override
    public byte[] handle(Field field) {
        mapPublisher.onNext(field);
        return field.getByteBuffer().array();
    }
}

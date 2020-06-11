package com.example.demo.handler;

import com.example.demo.Dispatcher;
import com.example.demo.model.Request;
import com.google.flatbuffers.Table;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHandler<T extends Table> implements Handler<T> {

    @Autowired
    Dispatcher demoHandlerFactory;
    Class<T> clazz;

    public byte[] handle(Request req) {
        try {
            return handle((T) req.getMessage().payload(clazz.newInstance()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }
}

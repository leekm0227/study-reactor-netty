package com.example.demo.handler;

import com.example.demo.Dispatcher;
import com.example.demo.model.RequestBean;
import com.google.flatbuffers.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public abstract class AbstractHandler<T extends Table> implements Handler<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);

    @Autowired
    Dispatcher demoHandlerFactory;

    Class<T> cls;

    public byte[] handle(RequestBean req) {
        try {
            return handle(req.getSid(),
                    (T) req.getMessage().payload(cls.newInstance()),
                    req.getMessage().method());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return empty();
    }

    public byte[] empty() {
        return new byte[0];
    }
}

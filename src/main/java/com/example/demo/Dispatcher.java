package com.example.demo;

import com.example.demo.handler.AbstractHandler;
import com.example.demo.model.RequestBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class Dispatcher {

    private HashMap<Byte, AbstractHandler> handlers;

    @PostConstruct
    public void init() {
        handlers = new HashMap<>();
    }

    public void register(byte bodyType, AbstractHandler handler) {
        handlers.put(bodyType, handler);
    }

    byte[] handle(RequestBean req) {
        AbstractHandler handler = handlers.get(req.getMessage().payloadType());
        return handler == null ? new byte[0] : handler.handle(req);
    }
}

package com.example.demo.handler;

import com.example.demo.fb.Action;
import com.example.demo.fb.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ActionHandler extends AbstractHandler<Action> {


    @PostConstruct
    public void init() {
        demoHandlerFactory.register(Payload.Action, this);
    }

    @Override
    public byte[] handle(Action chat) {
        return new byte[0];
    }
}

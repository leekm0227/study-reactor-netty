package com.example.demo.handler;

import com.example.demo.flatbuffer.FbAction;
import com.example.demo.flatbuffer.FbPayload;
import com.example.demo.flatbuffer.FbState;
import com.example.demo.model.FieldBean;
import com.example.demo.publisher.FieldPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ActionHandler extends AbstractHandler<FbAction> {

    @Autowired
    FieldPublisher fieldPublisher;

    @PostConstruct
    public void init() {
        cls = FbAction.class;
        demoHandlerFactory.register(FbPayload.FbAction, this);
    }

    @Override
    public byte[] handle(String sid, FbAction action, byte method) {
        switch (action.object().state()) {
            case FbState.A:

                break;
            case FbState.D:
                fieldPublisher.onNext(new FieldBean());
                break;
            case FbState.I:
            case FbState.M:
                fieldPublisher.update(action.object());
                break;
            case FbState.S:
                fieldPublisher.spawn(action.object());
                break;
        }

        return empty();
    }
}

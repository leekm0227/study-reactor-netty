package com.example.demo.publisher;

import com.example.demo.flatbuffer.FbObject;
import com.example.demo.flatbuffer.FbState;
import com.example.demo.flatbuffer.FbType;
import com.example.demo.model.FieldBean;
import com.example.demo.model.ObjectBean;
import com.example.demo.util.FbConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Random;

@Component
public class FieldPublisher {

    private UnicastProcessor<FieldBean> fieldPublisher;
    private Flux<FieldBean> fieldFlux;
    private FieldBean lastField;

    @PostConstruct
    public void init() {
        fieldPublisher = UnicastProcessor.create();
        fieldFlux = fieldPublisher.replay(1).autoConnect(0);
        initField();
    }

    public void onNext(FieldBean fieldBean) {
        System.out.println("updated field : " + fieldBean);
        lastField = fieldBean;
        fieldPublisher.onNext(fieldBean);
    }

    public Flux<byte[]> subscribe() {
        return fieldFlux.map(fieldBean -> FbConverter.toField(fieldBean).getByteBuffer().array());
    }

    void initField() {
        Random random = new Random();
        int length = 10;
        int x = random.nextInt(length) - length / 2;
        int y = random.nextInt(length) - length / 2;

        this.onNext(new FieldBean(new ArrayList<ObjectBean>() {{
            add(new ObjectBean("testoid000001", "mouse", FbType.N, FbState.I, new float[]{x, y, 0}));
        }}));
    }

    public void spawn(FbObject object) {
        lastField.addObject(new ObjectBean(
                object.oid(),
                object.name(),
                object.type(),
                object.state(),
                new float[]{object.pos().x(), object.pos().y(), object.pos().z()}
        ));

        this.onNext(lastField);
    }

    public void update(FbObject object) {
        System.out.println("obj name : " + object.name());

        lastField.getObjects().stream().filter(x -> x.getOid().equals(object.oid())).forEach(objectBean -> {
            objectBean.setState(object.state());
            objectBean.setPos(new float[]{object.pos().x(), object.pos().y(), object.pos().z()});
        });

        this.onNext(lastField);
    }
}

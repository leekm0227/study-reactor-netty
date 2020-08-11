package com.example.demo;

import com.example.demo.flatbuffer.FbType;
import com.example.demo.model.FieldBean;
import com.example.demo.publisher.FieldPublisher;
import com.example.demo.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DemoTask {

    @Autowired
    FieldPublisher fieldPublisher;

    @Scheduled(fixedRate = 100)
    public void task() {
        FieldBean fieldBean = fieldPublisher.getLastField();
        fieldBean.getObjects().stream()
                .filter(x -> x.getType() == FbType.N)
                .forEach(objectBean -> {
                            Random random = new Random();
                            int length = 10;
                            int x = random.nextInt(length) - length / 2;
                            int y = random.nextInt(length) - length / 2;

                            objectBean.setPos(Const.X, x);
                            objectBean.setPos(Const.Y, y);
                        }
                );

        fieldPublisher.onNext(fieldBean);
    }
}




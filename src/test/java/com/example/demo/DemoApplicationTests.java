package com.example.demo;


import com.example.demo.domain.Account;
import com.example.demo.flatbuffer.FbMessage;
import com.example.demo.flatbuffer.FbMethod;
import com.example.demo.flatbuffer.FbSignIn;
import com.example.demo.handler.ActionHandler;
import com.example.demo.handler.SignInHandler;
import com.example.demo.util.FbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.ByteBuffer;


@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

    @Autowired
    ActionHandler actionHandler;

    @Autowired
    SignInHandler signInHandler;

    @BeforeEach
    void before() {
    }

    @Test
    void testSignIn() {
        FbSignIn request = FbConverter.signIn(Account.builder().pid("testpid00001").build());
        byte[] bytes = signInHandler.handle(request, FbMethod.N);
        FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
        FbSignIn response = (FbSignIn) message.payload(new FbSignIn());

        System.out.println(response.uid());
    }
}

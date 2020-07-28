package com.example.demo;


import com.example.demo.domain.Account;
import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.*;
import com.example.demo.handler.CharacterHandler;
import com.example.demo.handler.SignInHandler;
import com.example.demo.publisher.ChatPublisher;
import com.example.demo.publisher.FieldPublisher;
import com.example.demo.util.FbConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.ByteBuffer;
import java.util.Arrays;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("api")
class DemoApplicationApiTests {

    @Autowired
    SignInHandler signInHandler;

    @Autowired
    CharacterHandler characterHandler;

    @Autowired
    ChatPublisher chatPublisher;

    @Autowired
    FieldPublisher fieldPublisher;

    @BeforeEach
    void before() {
    }

    @Test
    void testChat() {
        chatPublisher.subscribe().subscribe(bytes -> {
            FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
            FbChat chat = (FbChat) message.payload(new FbChat());
            System.out.println("test1 : " + chat.content());
        });

        chatPublisher.subscribe().subscribe(bytes -> {
            FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
            FbChat chat = (FbChat) message.payload(new FbChat());
            System.out.println("test2 : " + chat.content());
        });

        for (int i = 0; i < 100; i++) {
            chatPublisher.onNext(FbConverter.toChat("testcid", "testoid" + i, "msg content" + i));
        }
    }

    @Test
    void testField() {
        fieldPublisher.init();
        fieldPublisher.subscribe().subscribe(bytes -> {
            System.out.println(Arrays.toString(bytes));
            FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
            FbField field = (FbField) message.payload(new FbField());

            System.out.println("payload type : " + message.payloadType());
            assert field != null;
            System.out.println("obj len : " + field.objectsLength());
        });
    }

    @Test
    void testSignIn() {
        FbSignIn signRequest = FbConverter.toSignIn(Account.builder().pid("testpid00002").build());
        FbSignIn signResponse = (FbSignIn) FbMessage.getRootAsFbMessage(ByteBuffer.wrap(signInHandler.handle(signRequest, FbMethod.N))).payload(new FbSignIn());
        System.out.println("uid : " + signResponse.uid());
    }

    @Test
    void testCharacters() {
        // sign in
        FbSignIn signRequest = FbConverter.toSignIn(Account.builder().pid("testpid00002").build());
        FbSignIn signResponse = (FbSignIn) FbMessage.getRootAsFbMessage(ByteBuffer.wrap(signInHandler.handle(signRequest, FbMethod.N))).payload(new FbSignIn());
        System.out.println("uid : " + signResponse.uid());

        // before get characters
        FbCharacter getCharacterRequest = FbConverter.toCharacter(Account.builder().id(signResponse.uid()).build());
        FbCharacter getCharacterResponse = (FbCharacter) FbMessage.getRootAsFbMessage(ByteBuffer.wrap(characterHandler.handle(getCharacterRequest, FbMethod.R))).payload(new FbCharacter());
        System.out.println("before length : " + getCharacterResponse.objectsLength());

        // make character
        FbCharacter makeCharacterRequest = FbConverter.toCharacter(Account.builder().id(signResponse.uid()).character(Character.builder().name("test0002").build()).build());
        FbCharacter makeCharacterResponse = (FbCharacter) FbMessage.getRootAsFbMessage(ByteBuffer.wrap(characterHandler.handle(makeCharacterRequest, FbMethod.C))).payload(new FbCharacter());

        // after get characters
        FbCharacter afterCharacterRequest = FbConverter.toCharacter(Account.builder().id(signResponse.uid()).build());
        FbCharacter afterCharacterResponse = (FbCharacter) FbMessage.getRootAsFbMessage(ByteBuffer.wrap(characterHandler.handle(afterCharacterRequest, FbMethod.R))).payload(new FbCharacter());
        System.out.println("before length : " + afterCharacterResponse.objectsLength());
        System.out.println(afterCharacterResponse.objects(0).oid());
    }
}

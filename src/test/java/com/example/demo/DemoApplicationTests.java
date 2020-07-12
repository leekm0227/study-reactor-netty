package com.example.demo;


import com.example.demo.domain.Account;
import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.FbCharacter;
import com.example.demo.flatbuffer.FbMessage;
import com.example.demo.flatbuffer.FbMethod;
import com.example.demo.flatbuffer.FbSignIn;
import com.example.demo.handler.ActionHandler;
import com.example.demo.handler.CharacterHandler;
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

    @Autowired
    CharacterHandler characterHandler;

    @BeforeEach
    void before() {
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

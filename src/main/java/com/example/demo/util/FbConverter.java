package com.example.demo.util;

import com.example.demo.domain.Account;
import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.*;
import com.example.demo.model.FieldBean;
import com.example.demo.model.ObjectBean;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.Optional;

public final class FbConverter {

    public static FbField toField(FieldBean fieldBean) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int[] arrObject = new int[fieldBean.getObjects().size()];

        int index = 0;
        for (ObjectBean object : fieldBean.getObjects()) {
            int oid = builder.createString(object.getOid());
            int name = builder.createString(object.getName());
            FbObject.startFbObject(builder);
            FbObject.addOid(builder, oid);
            FbObject.addName(builder, name);
            FbObject.addPos(builder, FbVec3.createFbVec3(builder, object.getPos(Const.X), object.getPos(Const.Y), object.getPos(Const.Z)));
            FbObject.addState(builder, object.getState());
            FbObject.addType(builder, object.getType());
            int objectOffset = FbObject.endFbObject(builder);
            arrObject[index] = objectOffset;
            index++;
        }

        int objectsVector = FbField.createObjectsVector(builder, arrObject);
        int fieldOffset = FbField.createFbField(builder, objectsVector);
        int messageOffset = FbMessage.createFbMessage(builder, FbMethod.N, FbResult.S, FbPayload.FbField, fieldOffset);
        builder.finish(messageOffset);

        FbMessage message = FbMessage.getRootAsFbMessage(builder.dataBuffer());
        return (FbField) message.payload(new FbField());
    }

    public static FbSignIn toSignIn(Account account) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int pid = builder.createString(Optional.ofNullable(account.getPid()).orElse(""));
        int uid = builder.createString(Optional.ofNullable(account.getId()).orElse(""));
        int signInOffset = FbSignIn.createFbSignIn(builder, uid, pid);
        int messageOffset = FbMessage.createFbMessage(builder, FbMethod.N, FbResult.S, FbPayload.FbSignIn, signInOffset);
        builder.finish(messageOffset);

        FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(builder.sizedByteArray()));
        return (FbSignIn) message.payload(new FbSignIn());
    }

    public static FbCharacter toCharacter(Account account) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int[] arrChar = new int[account.getCharacters().size()];

        int index = 0;
        for (Character character : account.getCharacters()) {
            int oid = builder.createString(character.getId());
            int name = builder.createString(character.getName());
            FbObject.startFbObject(builder);
            FbObject.addOid(builder, oid);
            FbObject.addName(builder, name);
            FbObject.addPos(builder, FbVec3.createFbVec3(builder, character.getPos(Const.X), character.getPos(Const.Y), character.getPos(Const.Z)));
            FbObject.addState(builder, FbState.I);
            FbObject.addType(builder, FbType.P);
            int objectOffset = FbObject.endFbObject(builder);
            arrChar[index] = objectOffset;
            index++;
        }

        int uid = builder.createString(Optional.ofNullable(account.getId()).orElse(""));
        int objectsVector = FbCharacter.createObjectsVector(builder, arrChar);
        int signInOffset = FbCharacter.createFbCharacter(builder, uid, objectsVector);
        int messageOffset = FbMessage.createFbMessage(builder, FbMethod.N, FbResult.S, FbPayload.FbCharacter, signInOffset);
        builder.finish(messageOffset);

        FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(builder.sizedByteArray()));
        return (FbCharacter) message.payload(new FbCharacter());
    }

    public static FbChat toChat(String cid, String oid, String content) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int cidOffset = builder.createString(cid);
        int oidOffset = builder.createString(oid);
        int contentOffset = builder.createString(content);
        int chatOffset = FbChat.createFbChat(builder, cidOffset, oidOffset, contentOffset);
        int messageOffset = FbMessage.createFbMessage(builder, FbMethod.N, FbResult.S, FbPayload.FbChat, chatOffset);
        builder.finish(messageOffset);

        FbMessage message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(builder.sizedByteArray()));
        return (FbChat) message.payload(new FbChat());

    }
}

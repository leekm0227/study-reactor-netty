package com.example.demo.util;

import com.example.demo.flatbuffer.FbMessage;
import com.example.demo.model.RequestBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;


public class FbDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        FbMessage message = new FbMessage();

        try {
            message = FbMessage.getRootAsFbMessage(ByteBuffer.wrap(bytes));
        } catch (Exception ignored) {

        }

        out.add(new RequestBean(ctx.channel().id().toString(), message));
    }
}

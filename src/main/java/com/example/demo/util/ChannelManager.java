package com.example.demo.util;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);
    final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void addChannel(Channel channel) {
        channel.attr(AttributeKey.valueOf(channel.id().toString()));
        channels.add(channel);
        logger.info("channels size : {}", channels.size());
    }

    public int getSize() {
        return channels.size();
    }

    public boolean readable(String sid, String cid) {
        return channels.stream()
                .anyMatch(value -> value.id().toString().equals(sid) && value.hasAttr(AttributeKey.valueOf(cid)));
    }

    public void join(String sid, String cid) {
        channels.stream()
                .filter(value -> value.id().toString().equals(sid))
                .findFirst()
                .ifPresent(channel -> channel.attr(AttributeKey.valueOf(cid)));
    }

    public void leave(String sid, String cid) {
        channels.stream()
                .filter(value -> value.id().toString().equals(sid))
                .findFirst()
                .ifPresent(channel -> channel.attr(AttributeKey.valueOf(cid)).set(null));
    }
}

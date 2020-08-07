package com.example.demo.util;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.netty.Connection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);
    final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    final ConcurrentMap<Integer, String> connMap = new ConcurrentHashMap<>();

    public void onConnect(Connection conn) {
        connMap.put(conn.hashCode(), conn.channel().id().toString());
        conn.channel().attr(AttributeKey.valueOf(Const.TOPIC_NOTICE));
        conn.channel().attr(AttributeKey.valueOf(conn.channel().id().toString()));
        channels.add(conn.channel());
    }

    public void log() {
        channels.forEach(channel -> logger.info("channel id : {}", channel.id().toString()));
    }

    public boolean readable(int hash, String cid) {
        return channels.stream().anyMatch(value -> value.id().toString().equals(connMap.getOrDefault(hash, "")) && value.hasAttr(AttributeKey.valueOf(cid)));
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

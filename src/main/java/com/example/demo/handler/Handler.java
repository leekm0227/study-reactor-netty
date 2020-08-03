package com.example.demo.handler;

public interface Handler<T> {

    byte[] handle(String sid, T request, byte method);
}

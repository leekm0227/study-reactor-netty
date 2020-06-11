package com.example.demo.handler;

public interface Handler<T> {

    byte[] handle(T request);
}

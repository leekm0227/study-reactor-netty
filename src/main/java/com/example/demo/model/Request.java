package com.example.demo.model;

import com.example.demo.fb.Message;

public class Request {
    private String sid;
    private Message message;

    public Request(String sid, Message message) {
        this.sid = sid;
        this.message = message;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

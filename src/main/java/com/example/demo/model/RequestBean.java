package com.example.demo.model;

import com.example.demo.flatbuffer.FbMessage;

public class RequestBean extends AbstractBean {
    private String sid;
    private FbMessage message;

    public RequestBean(String sid, FbMessage message) {
        this.sid = sid;
        this.message = message;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public FbMessage getMessage() {
        return message;
    }

    public void setMessage(FbMessage message) {
        this.message = message;
    }
}

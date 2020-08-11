package com.example.demo.model;

public class ObjectBean extends AbstractBean {
    private String oid;
    private String name;
    private byte type;
    private byte state;
    private float[] pos;

    public ObjectBean(String id, String name, byte type, byte state, float[] pos) {
        this.oid = id;
        this.name = name;
        this.type = type;
        this.state = state;
        this.pos = pos;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public float getPos(int pos) {
        return this.pos[pos];
    }

    public void setPos(int pos, float value) {
        this.pos[pos] = value;
    }
}

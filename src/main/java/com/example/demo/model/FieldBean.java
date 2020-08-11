package com.example.demo.model;

import java.util.ArrayList;
import java.util.Collection;

public class FieldBean extends AbstractBean {
    Collection<ObjectBean> objects;

    public FieldBean(Collection<ObjectBean> objects) {
        this.objects = objects;
    }

    public FieldBean() {
        this.objects = new ArrayList<>();
    }

    public void addObject(ObjectBean object) {
        objects.add(object);
    }

    public Collection<ObjectBean> getObjects() {
        return objects;
    }

}

package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@EqualsAndHashCode(callSuper = true)
public class Character extends AbstractDomain {

    String name;

    @Builder.Default
    int level = 1;

    @Builder.Default
    int exp = 0;

    @Builder.Default
    float[] pos = new float[]{0, 0, 0};

    public float getPos(int pos) {
        return this.pos[pos];
    }

    public void setPos(int pos, int value) {
        this.pos[pos] = value;
    }
}

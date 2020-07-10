package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
abstract class AbstractDomain implements Serializable {

    @Id
    protected String id;

    @CreatedDate
    protected LocalDateTime createAt;

    @LastModifiedDate
    protected LocalDateTime modifyAt;
}

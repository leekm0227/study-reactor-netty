package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@EqualsAndHashCode(callSuper = true)
public class Item extends AbstractDomain {

    int tid;
    long amount;
}
package com.example.demo.domain;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Data
@Builder
@Document
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractDomain {
    String pid;
    int type;

    @Singular
    Collection<Character> characters;

    public void addCharacter(Character character) {
        this.characters.add(character);
    }
}

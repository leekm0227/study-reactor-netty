package com.example.demo.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Data
@Document
@SuperBuilder
@NoArgsConstructor
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

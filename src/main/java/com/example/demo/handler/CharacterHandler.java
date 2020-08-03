package com.example.demo.handler;

import com.example.demo.domain.Account;
import com.example.demo.domain.Character;
import com.example.demo.flatbuffer.FbCharacter;
import com.example.demo.flatbuffer.FbMethod;
import com.example.demo.flatbuffer.FbObject;
import com.example.demo.flatbuffer.FbPayload;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.Const;
import com.example.demo.util.FbConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

@Component
public class CharacterHandler extends AbstractHandler<FbCharacter> {

    @Autowired
    AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        cls = FbCharacter.class;
        demoHandlerFactory.register(FbPayload.FbCharacter, this);
    }

    @Override
    public byte[] handle(String sid, FbCharacter request, byte method) {
        Optional<Account> account = accountRepository.findById(Objects.requireNonNull(request.uid()));

        if (account.isPresent()) {
            switch (method) {
                case FbMethod.C:
                    return addCharacter(account.get(), request.objects(0));
                case FbMethod.D:
                    return delCharacter(account.get(), request.objects(0));
                case FbMethod.R:
                    return FbConverter.toCharacter(account.get()).getByteBuffer().array();
            }
        }

        return empty();
    }

    private byte[] addCharacter(Account account, FbObject object) {
        if (account.getCharacters().size() <= Const.MAX_CHARACTER_COUNT) {
            account.addCharacter(Character.builder()
                    .name(object.name())
                    .build());

            Account savedAccount = accountRepository.save(account);
            return FbConverter.toCharacter(savedAccount).getByteBuffer().array();
        }

        return empty();
    }

    private byte[] delCharacter(Account account, FbObject object) {
        Optional<Character> target = account.getCharacters().stream().filter(x -> x.getId().equals(object.oid())).findFirst();

        if (target.isPresent()) {
            account.getCharacters().remove(target.get());
            Account savedAccount = accountRepository.save(account);
            return FbConverter.toCharacter(savedAccount).getByteBuffer().array();
        }

        return empty();
    }
}

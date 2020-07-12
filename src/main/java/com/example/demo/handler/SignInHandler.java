package com.example.demo.handler;

import com.example.demo.domain.Account;
import com.example.demo.flatbuffer.FbPayload;
import com.example.demo.flatbuffer.FbSignIn;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.FbConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
public class SignInHandler extends AbstractHandler<FbSignIn> {

    @Autowired
    AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        cls = FbSignIn.class;
        demoHandlerFactory.register(FbPayload.FbSignIn, this);
    }

    @Override
    public byte[] handle(FbSignIn request, byte method) {
        Optional<Account> account = accountRepository.findByPid(request.pid());

        Account response = account.orElseGet(() -> accountRepository.save(
                Account.builder()
                        .pid(request.pid())
                        .type(0)
                        .build()
        ));

        return FbConverter.toSignIn(response).getByteBuffer().array();
    }
}

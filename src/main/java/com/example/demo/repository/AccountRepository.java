package com.example.demo.repository;

import com.example.demo.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, String> {

    Optional<Account> findByPid(String pid);
}

package com.eaglebank.api.repository;

import com.eaglebank.api.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    List<BankAccount> findByUserId(String userId);
    Optional<BankAccount> findByAccountNumberAndUserId(String accountNumber, String userId);
    boolean existsByUserId(String userId);
}


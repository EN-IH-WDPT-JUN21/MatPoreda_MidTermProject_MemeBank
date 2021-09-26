package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.accounts.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Long> {
    Optional<Savings> findBySecretKey(String secretKey);
}

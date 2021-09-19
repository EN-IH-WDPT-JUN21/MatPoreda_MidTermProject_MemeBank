package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long>{
    Optional<AccountHolder> findByName(String name);
}

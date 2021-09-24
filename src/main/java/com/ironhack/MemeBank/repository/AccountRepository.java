package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findBySecretKey(String secretKey);
    List<Account> findByPrimaryOwnerOrSecondaryOwner(User primaryOwner, User secondaryOwner);
}

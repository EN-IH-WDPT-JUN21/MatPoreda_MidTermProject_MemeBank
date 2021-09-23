package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long>{
    Optional<ThirdParty> findByHashKey(String hashKey);
    Optional<ThirdParty> findByUsername(String name);
}

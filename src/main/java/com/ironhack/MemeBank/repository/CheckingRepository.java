package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.accounts.Checking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Long> {

//    List<Checking> findByAccountHolder(AccountHolder accountHolder);
}

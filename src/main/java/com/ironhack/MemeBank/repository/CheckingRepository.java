package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.accounts.Checking;
import com.ironhack.MemeBank.dao.accounts.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Long> {
}

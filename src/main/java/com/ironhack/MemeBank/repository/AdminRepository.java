package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.AccountHolder;
import com.ironhack.MemeBank.dao.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}

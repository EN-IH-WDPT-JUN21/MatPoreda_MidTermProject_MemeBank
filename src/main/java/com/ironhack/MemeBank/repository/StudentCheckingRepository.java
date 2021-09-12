package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.AccountHolder;
import com.ironhack.MemeBank.dao.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {
}

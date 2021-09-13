package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.accounts.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Long> {
}

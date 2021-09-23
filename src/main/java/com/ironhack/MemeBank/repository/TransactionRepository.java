package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value="SELECT t.* FROM transaction t WHERE t.account_account_id=:account_id AND t.type=:type AND MONTH(t.date)=MONTH(:date) AND YEAR(t.date)=YEAR(:date) ORDER BY t.date DESC limit 1", nativeQuery = true)
    Optional<Transaction> findLastTransactionWithGivenMonthAndTypeAndAccountId(
            @Param("account_id") Long account_id,
            @Param("type") Integer type,
            @Param("date") LocalDateTime date
            );

    @Query(value="SELECT t.* FROM transaction t WHERE t.account_account_id=:account_id AND t.type=:type ORDER BY t.date DESC limit 1", nativeQuery = true)
    Optional<Transaction> findLastAccrualTransaction(
            @Param("account_id") Long account_id,
            @Param("type") Integer type
    );

    List<Transaction> findByTransactionInitiator(User user);


}

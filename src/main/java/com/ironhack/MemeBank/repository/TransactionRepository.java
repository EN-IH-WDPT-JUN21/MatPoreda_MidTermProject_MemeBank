package com.ironhack.MemeBank.repository;

import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query(value="SELECT t.* FROM transaction t WHERE t.account_account_id=:account_id AND t.type=6 ORDER BY t.date DESC limit 1", nativeQuery = true)
    Optional<Transaction> findLastMonthlyMaintenanceFee(
            @Param("account_id") Long account_id
    );

    @Query(value="SELECT t.* FROM transaction t WHERE t.account_account_id=:account_id AND t.type=4 ORDER BY t.date DESC limit 1", nativeQuery = true)
    Optional<Transaction> findLastInterestRatesAccrual(
            @Param("account_id") Long account_id
    );

    @Query(value="select IFNULL(MAX(t.amount),0) max_volume from (SELECT transaction_initiator_user_id, DATE(date), SUM(amount) amount FROM TRANSACTION  WHERE transaction_initiator_user_id=2 AND type=2 AND status=1 GROUP BY  transaction_initiator_user_id, DATE(date)) t", nativeQuery = true)
    BigDecimal findMaxDailyVolume(
            @Param("user_id") Long user_id
    );

    @Query(value="SELECT IFNULL(SUM(amount), 0) amount FROM TRANSACTION  WHERE transaction_initiator_user_id=:user_id AND type=2 AND status=1 AND (date >= NOW() - INTERVAL 1 DAY)", nativeQuery = true)
    BigDecimal findTransactionVolumeInLast24H(
            @Param("user_id") Long user_id
    );

    List<Transaction> findByTransactionInitiator(User user);


}

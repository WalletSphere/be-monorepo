package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositWithdrawalTransactionsHistoryRepository extends JpaRepository<ExchangerDepositWithdrawalTransactions, Long> {

    List<ExchangerDepositWithdrawalTransactions> findAllByUserId(long userId);
    Optional<ExchangerDepositWithdrawalTransactions> findByBalance_Id(long balanceId);
    void deleteAllByBalance_Id(long balanceId);
}

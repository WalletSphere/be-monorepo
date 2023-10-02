package com.khomishchak.cryptoportfolio.repositories;

import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

}

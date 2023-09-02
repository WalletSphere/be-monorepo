package com.khomishchak.cryproportfolio.repositories;

import com.khomishchak.cryproportfolio.model.exchanger.Balance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

}

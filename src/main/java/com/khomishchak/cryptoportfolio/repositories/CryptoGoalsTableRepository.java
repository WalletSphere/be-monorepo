package com.khomishchak.cryptoportfolio.repositories;

import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoGoalsTableRepository extends JpaRepository<CryptoGoalsTable, Long> {

}

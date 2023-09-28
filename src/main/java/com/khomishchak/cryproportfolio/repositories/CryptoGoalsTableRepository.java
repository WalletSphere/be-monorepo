package com.khomishchak.cryproportfolio.repositories;

import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsTable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoGoalsTableRepository extends JpaRepository<CryptoGoalsTable, Long> {

}

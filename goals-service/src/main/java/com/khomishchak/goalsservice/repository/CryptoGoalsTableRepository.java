package com.khomishchak.goalsservice.repository;

import com.khomishchak.goalsservice.model.CryptoGoalsTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CryptoGoalsTableRepository extends JpaRepository<CryptoGoalsTable, Long> {
    Optional<CryptoGoalsTable> findByUserId(long userId);
}

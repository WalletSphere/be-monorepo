package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.goals.CryptoGoalsTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoGoalsTableRepository extends JpaRepository<CryptoGoalsTable, Long> {

}

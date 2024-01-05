package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.ExchangerUniqueCurrenciesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByCodeAndUser_Id(ExchangerCode code, Long userId);
    List<Balance> findAllByUser_Id(Long userId);

    @Query("SELECT new com.khomishchak.ws.model.exchanger.ExchangerUniqueCurrenciesDTO(b.code, STRING_AGG(bc.currencyCode, ', ')) " +
            "FROM Balance b JOIN b.currencies bc GROUP BY b.code")
    List<ExchangerUniqueCurrenciesDTO> getAllUsedCurrencyCodes();

}

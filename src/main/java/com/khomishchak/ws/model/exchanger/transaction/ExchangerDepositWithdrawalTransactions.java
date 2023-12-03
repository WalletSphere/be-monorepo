package com.khomishchak.ws.model.exchanger.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "deposit_withdrawal_transactions_history")
public class ExchangerDepositWithdrawalTransactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ExchangerCode code;

    private long userId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @OneToMany(mappedBy = "exchangerDepositWithdrawalTransactions", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepositWithdrawalTransaction> transactions;

    @JsonIgnore
    public Balance getBalance() {
        return balance;
    }

    public Long getBalanceId() {
        return balance.getId();
    }
}

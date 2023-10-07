package com.khomishchak.cryptoportfolio.model;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    private String transactionId;
    private String transactionHash;
    private String ticker;
    private BigDecimal fee;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}

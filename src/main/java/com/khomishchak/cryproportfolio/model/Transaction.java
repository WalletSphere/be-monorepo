package com.khomishchak.cryproportfolio.model;

import org.springframework.stereotype.Service;

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
    private double fee;
    private double amount;
    private Date createdAt;
}

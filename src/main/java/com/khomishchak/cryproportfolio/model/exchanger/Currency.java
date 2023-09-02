package com.khomishchak.cryproportfolio.model.exchanger;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    private String currencyCode;

    private double amount;

    @Transient
    private double totalValue;
}

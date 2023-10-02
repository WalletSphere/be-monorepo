package com.khomishchak.cryptoportfolio.model.goals;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class CryptoGoalsTableRecord {
    private String name;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal goalQuantity;

    @Transient
    private BigDecimal donePercentage;

    @Transient
    private BigDecimal leftToBuy;

    @Transient
    private boolean finished;
}

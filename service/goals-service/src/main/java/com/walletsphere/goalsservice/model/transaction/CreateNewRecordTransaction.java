package com.walletsphere.goalsservice.model.transaction;

import java.math.BigDecimal;

public record CreateNewRecordTransaction(String name, BigDecimal quantity, BigDecimal price, BigDecimal goalQuantity) {

    public CreateNewRecordTransaction {
        if (quantity != null && BigDecimal.ZERO.compareTo(quantity) > 0) {
            throw new IllegalArgumentException("Quantity cannot be less than zero.");
        }
        if (goalQuantity != null && BigDecimal.ZERO.compareTo(goalQuantity) > 0) {
            throw new IllegalArgumentException("Goal Quantity cannot be less than zero.");
        }
    }
}

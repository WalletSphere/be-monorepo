package com.walletsphere.goalsservice.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionChangeStateDTO(BigDecimal oldRecordQuantity, BigDecimal oldRecordAveragePrice,
                                        BigDecimal newOperationQuantity, BigDecimal newOperationAveragePrice,
                                        TransactionType transactionType) {
}

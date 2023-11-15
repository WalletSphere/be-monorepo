package com.khomishchak.cryptoportfolio.model.goals;

import com.khomishchak.cryptoportfolio.model.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionChangeStateDTO(BigDecimal oldRecordQuantity, BigDecimal oldRecordAveragePrice,
                                        BigDecimal newOperationQuantity, BigDecimal newOperationAveragePrice,
                                        TransactionType transactionType) {
}

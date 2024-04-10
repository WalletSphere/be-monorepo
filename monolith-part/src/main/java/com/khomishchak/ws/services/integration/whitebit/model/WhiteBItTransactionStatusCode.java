package com.khomishchak.ws.services.integration.whitebit.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum WhiteBItTransactionStatusCode {
    COMPLETED(Set.of(3, 7)),
    CANCELED(Set.of(4, 9)),
    UNCONFIRMED_BY_USER(Set.of(5)),
    AML_FROZEN(Set.of(21)),
    UNCREDITED(Set.of(22)),
    PENDING(Set.of(1, 2, 6, 10, 11, 12, 13, 14, 15, 16, 17)),
    PARTIALLY_SUCCESSFUL(Set.of(18));


    private final Set<Integer> codes;

    WhiteBItTransactionStatusCode(Set<Integer> codes) {
        this.codes = codes;
    }

    public Set<Integer> getCodes() {
        return this.codes;
    }

    public static WhiteBItTransactionStatusCode getStatusByCode(int code) {
        for (WhiteBItTransactionStatusCode status : WhiteBItTransactionStatusCode.values()) {
            if (status.getCodes().contains(code)) {
                return status;
            }
        }
        throw new RuntimeException(String.format("invalide transaction code used, %d", code));
    }
}

package com.khomishchak.cryptoportfolio.services.integration.whitebit.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiteBitDepositWithdrawalHistoryResp {

    private List<Record> records;

    @JsonCreator
    public WhiteBitDepositWithdrawalHistoryResp(@JsonProperty("records") List<Record> records) {
        this.records = records;
    }

    public List<Record> getRecords() {
        return records;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record {
        private String address;
        private Long createdAt;
        private String ticker;
        private int method;
        private double amount;
        private int status;
        private String transactionId;
        private String transactionHash;

        @JsonCreator
        public Record(@JsonProperty("address") String address, @JsonProperty("createdAt") Long createdAt,
                @JsonProperty("ticker") String ticker, @JsonProperty("method") int method,
                @JsonProperty("amount") double amount, @JsonProperty("status") int status,
                @JsonProperty("transactionId") String transactionId, @JsonProperty("transactionHash") String transactionHash) {
            this.address = address;
            this.createdAt = createdAt;
            this.ticker = ticker;
            this.method = method;
            this.amount = amount;
            this.status = status;
            this.transactionId = transactionId;
            this.transactionHash = transactionHash;
        }

        public String getAddress() {
            return address;
        }

        public Long getCreatedAt() {
            return createdAt;
        }

        public String getTicker() {
            return ticker;
        }

        public int getMethod() {
            return method;
        }

        public double getAmount() {
            return amount;
        }

        public int getStatus() {
            return status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getTransactionHash() {
            return transactionHash;
        }
    }
}

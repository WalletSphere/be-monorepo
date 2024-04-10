package com.khomishchak.authservice.model.auth.resp;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResp {
    private String errorType;
    private String errorMessage;
    private List<ErrorDetail> details;

    public ErrorResp(@JsonProperty("type") String errorType,
                     @JsonProperty("message") String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.details = new ArrayList<>();
    }

    public ErrorResp(@JsonProperty("type") String errorType,
                     @JsonProperty("message") String errorMessage,
                     @JsonProperty("details") List<ErrorDetail> details) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.details = details;
    }

    @Getter
    public static class ErrorDetail {
        private String key;
        private Object value;

        @JsonAnySetter
        public void setDetails(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}

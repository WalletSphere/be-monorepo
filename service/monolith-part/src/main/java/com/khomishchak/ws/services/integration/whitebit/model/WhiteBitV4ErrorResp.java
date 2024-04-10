package com.khomishchak.ws.services.integration.whitebit.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class WhiteBitV4ErrorResp {

    private String code;
    private String errorMessage;
    private Errors errors;

    public static class Errors {
        private List<String> messages = new ArrayList<>();

        @JsonAnySetter
        public void setMessages(Map<String, String> params) {
            messages.addAll(params.values());
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    @JsonCreator
    public WhiteBitV4ErrorResp(@JsonProperty("code") String code, @JsonProperty("message") String errorMessage,
                               @JsonProperty("errors") Errors errors) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.errors = errors;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Errors getErrors() {
        return errors;
    }
}

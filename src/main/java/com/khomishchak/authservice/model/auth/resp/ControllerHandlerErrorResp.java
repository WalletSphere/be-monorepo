package com.khomishchak.authservice.model.auth.resp;

import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

public class ControllerHandlerErrorResp {

    private String message;
    private String type;
    private List<ErrorDetail> details;

    public ControllerHandlerErrorResp() {
        this.details = new ArrayList<>();
    }

    public ControllerHandlerErrorResp(String message, String type) {
        this();
        this.message = message;
        this.type = type;
    }

    public static ControllerHandlerErrorResp fromValidationException(MethodArgumentNotValidException ex) {
        ControllerHandlerErrorResp response = new ControllerHandlerErrorResp("Validation error", "VALIDATION_FAILED");
        ex.getBindingResult().getAllErrors().forEach(error ->
                response.addDetail("validationErrors", error.getDefaultMessage()));
        return response;
    }

    public void addDetail(String key, Object value) {
        this.details.add(new ErrorDetail(key, value));
    }

    @Getter
    public static class ErrorDetail {
        private String key;
        private Object value;

        public ErrorDetail(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}

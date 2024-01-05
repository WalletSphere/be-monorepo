package com.khomishchak.ws.model.response;

import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ErrorResp {

    private String message;
    private String type;
    private List<ErrorDetail> details;

    public ErrorResp() {
        this.details = new ArrayList<>();
    }

    public ErrorResp(String message, String type) {
        this();
        this.message = message;
        this.type = type;
    }

    public static ErrorResp fromValidationException(MethodArgumentNotValidException ex) {
        ErrorResp response = new ErrorResp("Validation error", "VALIDATION_FAILED");
        ex.getBindingResult().getAllErrors().forEach(error ->
                response.addDetail("validationErrors", error.getDefaultMessage()));
        return response;
    }

    public static ErrorResp fromWhiteBitClientException(WhiteBitClientException ex) {
        ErrorResp response = new ErrorResp(ex.getMessage(), ex.getCode());
        List<String> errors = ex.getErrors();
        for (int i = 0; i < errors.size(); i++) {
            response.addDetail(String.format("detail%i", i), errors.get(i));
        }
        return response;
    }

    public static ErrorResp fromAuthenticationException(AuthenticationException ex, String errorType) {
        return new ErrorResp(ex.getMessage(), errorType);
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

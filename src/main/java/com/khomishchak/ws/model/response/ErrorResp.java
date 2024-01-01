package com.khomishchak.ws.model.response;

import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ErrorResp {

    private String message;
    private String code;
    private Map<String, Object> details;

    public ErrorResp() {
        this.details = new HashMap<>();
    }

    public ErrorResp(String message, String code) {
        this();
        this.message = message;
        this.code = code;
    }

    public static ErrorResp fromValidationException(MethodArgumentNotValidException ex) {
        ErrorResp response = new ErrorResp("Validation error", "VALIDATION_FAILED");
        ex.getBindingResult().getAllErrors().forEach(error ->
                response.addDetail("validationErrors", error.getDefaultMessage()));
        return response;
    }

    public static ErrorResp fromWhiteBitClientException(WhiteBitClientException ex) {
        ErrorResp response = new ErrorResp(ex.getMessage(), ex.getCode());
        response.setDetails(Map.of("errors", ex.getErrors()));
        return response;
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
}

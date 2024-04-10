package com.khomishchak.ws.services.integration.whitebit.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class WhiteBitErrorResponseMapper {
    private final ObjectMapper objectMapper;

    public WhiteBitErrorResponseMapper(@Qualifier("WhiteBitObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T mapPlainTextErrorToObj(String plainTextErrorMessage, Class<T> valueType) {
        try {
            return objectMapper.readValue(plainTextErrorMessage, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing error message JSON", e);
        }
    }
}

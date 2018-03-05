package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiAbstractModel implements ApiModelInterface {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String toJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

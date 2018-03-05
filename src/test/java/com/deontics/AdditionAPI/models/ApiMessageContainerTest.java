package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiMessageContainerTest {

    @Test
    public void basicTest() throws JsonProcessingException {

        String message = "Status: Success";
        String message2 = "Status: ERROR: An error message";
        String jsonResult = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiMessageContainer\",\"message\":\"Status: Success\"}";
        String jsonResult2 = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiMessageContainer\",\"message\":\"Status: ERROR: An error message\"}";

        ApiMessageContainer amc = new ApiMessageContainer(message);

        assertEquals(message, amc.getMessage());
        assertEquals(jsonResult, amc.toJson());

        amc.setMessage(message2);

        assertEquals(message2, amc.getMessage());
        assertEquals(jsonResult2, amc.toJson());
    }

}
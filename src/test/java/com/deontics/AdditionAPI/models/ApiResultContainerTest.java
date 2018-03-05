package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApiResultContainerTest {

    @Test
    public void basicTest() throws JsonProcessingException {

        Double result = 3.1415;
        Double result2 = 1.618;
        String jsonResult = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiResultContainer\",\"result\":3.1415}";
        String jsonResult2 = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiResultContainer\",\"result\":1.618}";

        ApiResultContainer arc = new ApiResultContainer(result);

        assertEquals(result, arc.getResult(), 0);
        assertEquals(jsonResult, arc.toJson());

        arc.setResult(result2);

        assertEquals(result2, arc.getResult(), 0);
        assertEquals(jsonResult2, arc.toJson());
    }

}
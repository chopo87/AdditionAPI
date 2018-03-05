package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApiRequestArrayTest {

    @Test
    public void basicTest() throws JsonProcessingException {

        String jsonResult = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81]}";
        String jsonResult2 = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81,1.618,2.99]}";
        String jsonResult3 = "{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[]}";
        List<Double> myList = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81));
        List<Double> myList2 = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81, 1.618));

        ApiRequestArray ara = new ApiRequestArray(myList);
        assertEquals(myList, ara.getSumList());
        assertEquals(jsonResult, ara.toJson());

        ara.setSumList(myList2);

        assertEquals(myList2, ara.getSumList());

        ara.addToSumList(2.99);
        assertEquals(jsonResult2, ara.toJson());

        ApiRequestArray ara2 = new ApiRequestArray();
        assertEquals(jsonResult3, ara2.toJson());

        ara2.setSumList(myList);
        assertEquals(jsonResult, ara2.toJson());
    }
}
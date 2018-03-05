package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ApiTransferContainerTest {

    @Test
    public void basicTest() throws JsonProcessingException {
        ApiSession.resetSessionIdCounter();
        String sessionIP = "192.168.1.1";
        String jsonResult = "{\"session\":null,\"data\":[],\"transmissionType\":\"SINGLE\"}";
        String jsonResult2 = "{\"session\":{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"},\"data\":[],\"transmissionType\":\"SINGLE\"}";
        String jsonResult3 = "{\"session\":{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"},\"data\":[{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81]},{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81,1.618]}],\"transmissionType\":\"BATCH\"}";
        String jsonResult4 = "{\"session\":{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"},\"data\":[{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81]},{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81,1.618]},{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,6.67,2.99]}],\"transmissionType\":\"BATCH\"}";
        String jsonResult5 = "{\"session\":{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"},\"data\":[{\"@class\":\"com.deontics.AdditionAPI.models.ApiResultContainer\",\"result\":6.63}],\"transmissionType\":\"SINGLE\"}";
        String jsonResult6 = "{\"session\":{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"},\"data\":[{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81]},{\"@class\":\"com.deontics.AdditionAPI.models.ApiRequestArray\",\"sumList\":[87.2,42.0,9.81,1.618]}],\"transmissionType\":\"BATCH\"}";
        List<Double> myList = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81));
        List<Double> myList2 = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81, 1.618));
        List<Double> myList3 = new ArrayList<>(Arrays.asList(87.2, 6.67, 2.99));

        ApiSession session = new ApiSession(sessionIP);
        ApiRequestArray ara = new ApiRequestArray(myList);
        ApiRequestArray ara2 = new ApiRequestArray(myList2);
        ApiRequestArray ara3 = new ApiRequestArray(myList3);

        ApiResultContainer arc = new ApiResultContainer(6.63);

        List<ApiAbstractTransferModel> dataList = new ArrayList<>(Arrays.asList(ara, ara2));

        ApiTransferContainer atc = new ApiTransferContainer();

        atc.setTransmissionType(ApiTransferContainer.TransmissionType.SINGLE);

        assertEquals(ApiTransferContainer.TransmissionType.SINGLE, atc.getTransmissionType());
        assertEquals(jsonResult,atc.toJson());

        atc.setSession(session);

        assertEquals(sessionIP, atc.getSession().getClientIP());
        assertEquals(jsonResult2,atc.toJson());

        atc.setData(dataList);
        assertEquals(jsonResult3,atc.toJson());

        List<ApiAbstractTransferModel> recoveredList = atc.getData();
        ApiRequestArray rara = (ApiRequestArray) recoveredList.get(1);
        assertEquals(1.618, rara.getSumList().get(3), 0);

        atc.addData(ara3);
        assertEquals(jsonResult4,atc.toJson());

        ApiTransferContainer atc2 = new ApiTransferContainer(session, arc);
        assertEquals(jsonResult5,atc2.toJson());

        ApiTransferContainer atc3 = new ApiTransferContainer(session, dataList);
        assertEquals(jsonResult6,atc3.toJson());

        ApiSession.resetSessionIdCounter();
    }





}
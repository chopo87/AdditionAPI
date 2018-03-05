package com.deontics.AdditionAPI.services;

import com.deontics.AdditionAPI.models.ApiAbstractTransferModel;
import com.deontics.AdditionAPI.models.ApiRequestArray;
import com.deontics.AdditionAPI.models.ApiResultContainer;
import com.deontics.AdditionAPI.models.ApiSession;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;

public class AdditionServiceTest {

    private static final ConcurrentMap<String, ApiResultContainer> resultCacheMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, ApiSession> sessionMap = new ConcurrentHashMap<>();



    @Test
    public void basicTest() throws Exception {

        String sessionIP = "192.168.1.1";
        List<Double> mySumList = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81));
        AdditionEngineApiService additionService = new AdditionService(resultCacheMap, sessionMap);

        ApiSession session = additionService.createSession(sessionIP);

        ApiRequestArray ara = new ApiRequestArray(mySumList);

        ApiAbstractTransferModel aatm = additionService.calculate(ara, session).getData().get(0);
        assertEquals(ApiResultContainer.class, aatm.getClass());

        ApiResultContainer arc = (ApiResultContainer) aatm;
        assertEquals(139.01, arc.getResult(), 0);
    }/**/

}
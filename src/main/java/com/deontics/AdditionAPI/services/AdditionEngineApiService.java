package com.deontics.AdditionAPI.services;

import com.deontics.AdditionAPI.models.ApiRequestArray;
import com.deontics.AdditionAPI.models.ApiSession;
import com.deontics.AdditionAPI.models.ApiTransferContainer;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AdditionEngineApiService {

    ApiSession createSession(String clientIP);

    void drop(Integer sessionId);

    ApiTransferContainer calculate(ApiRequestArray apiRequestArray, ApiSession apiSession) throws IllegalApiCallException;

    @Async
    public void batchCalculate(List<ApiRequestArray> apiRequestArrayList, Integer sessionId) throws IllegalApiCallException;
}

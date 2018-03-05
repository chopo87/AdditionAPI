package com.deontics.AdditionAPI.services;

import com.deontics.AdditionAPI.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class AdditionService implements AdditionEngineApiService {

    private ConcurrentMap<String, ApiResultContainer> resultCacheMap;
    private ConcurrentMap<Integer, ApiSession> sessionMap;

    @Autowired
    private ConcurrentMap<Integer, ApiTransferContainer> batchResultMap;

    @Autowired
    public AdditionService(ConcurrentMap<String, ApiResultContainer> resultCacheMap, ConcurrentMap<Integer, ApiSession> sessionMap) {
        this.resultCacheMap = resultCacheMap;
        this.sessionMap = sessionMap;
    }

    @Override
    public ApiSession createSession(String clientIP) {
        ApiSession apiSession = new ApiSession(clientIP);
        sessionMap.putIfAbsent(apiSession.getId(), apiSession);

        return apiSession;
    }

    @Override
    public void drop(Integer sessionId) {
        sessionMap.remove(sessionId);
    }

    @Override
    public ApiTransferContainer calculate(ApiRequestArray apiRequestArray, ApiSession apiSession) throws IllegalApiCallException {

        if (!sessionMap.containsKey(apiSession.getId()))
            throw new IllegalApiCallException("Invalid Session ID");

        ApiSession localApiSession = sessionMap.get(apiSession.getId());
        if (localApiSession.getStatus() != ApiSession.SessionStatus.AVAILABLE)
            throw new IllegalApiCallException("Session "
                    + apiSession.getId() + ", " + apiSession.getClientIP()
                    + " is currently BATCH PROCESSING an existing requests, no more requests can be accepted at this time");

        if (apiSession.getResult() != 0)
            apiRequestArray.addToSumList((Double) apiSession.getResult());

        return new ApiTransferContainer(
                apiSession,
                apiEngine(apiSession, apiRequestArray)
        );

    }

    @Async
    @Override
    public void batchCalculate(List<ApiRequestArray> apiRequestArrayList, Integer sessionId) throws IllegalApiCallException {

        if (!sessionMap.containsKey(sessionId))
            throw new IllegalApiCallException("Invalid Session ID");

        ApiSession apiSession = sessionMap.get(sessionId);
        if (apiSession.getStatus() != ApiSession.SessionStatus.AVAILABLE)
            throw new IllegalApiCallException("Session "
                    + sessionId + ", " + apiSession.getClientIP()
                    + " is currently BATCH PROCESSING an existing requests, no more requests can be accepted at this time");

        apiSession.setStatus(ApiSession.SessionStatus.BATCH_PROCESSING);

        sessionMap.replace(sessionId, apiSession);

        List<ApiAbstractTransferModel> atml = apiRequestArrayList.stream().map(ara -> {
            try {
                return apiEngine(apiSession, ara);
            } catch (IllegalApiCallException e) {
                return new ApiMessageContainer(e.getMessage());
            }
        }).collect(Collectors.toList());

        ApiTransferContainer atc = new ApiTransferContainer(apiSession, atml);
        batchResultMap.put(apiSession.getId(), atc);

        apiSession.setStatus(ApiSession.SessionStatus.AWAITING_RETRIEVAL);

    }

    public ApiResultContainer apiEngine(ApiSession apiSession, ApiRequestArray apiRequestArray) throws IllegalApiCallException {

        if (apiSession.getResult() != 0)
            apiRequestArray.addToSumList((Double) apiSession.getResult());

        String jsonKey = null;
        try {
            jsonKey = apiRequestArray.toJson();
        } catch (JsonProcessingException e) {
            throw new IllegalApiCallException("Invalid Sum Array Format");
        }

        ApiResultContainer arc;
        if (resultCacheMap.containsKey(jsonKey))
            arc = resultCacheMap.get(jsonKey);
        else {
            arc = new ApiResultContainer(
                    apiRequestArray.getSumList().stream().reduce(0.0, Double::sum)
            );
            resultCacheMap.put(jsonKey, arc);
        }

        apiSession.setResult(arc.getResult());
        return arc;
    }
}

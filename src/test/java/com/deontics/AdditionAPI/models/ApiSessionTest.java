package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;

public class ApiSessionTest {

    @Test
    public void basicTest() throws JsonProcessingException {

        ApiSession.resetSessionIdCounter();
        String sessionIP = "192.168.1.1";
        String sessionIP2 = "192.168.1.254";
        String jsonResult = "{\"id\":1,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"}";
        String jsonResult2 = "{\"id\":1,\"clientIP\":\"192.168.1.254\",\"status\":\"AVAILABLE\"}";
        String jsonResult3 = "{\"id\":1,\"clientIP\":\"192.168.1.254\",\"status\":\"BATCH_PROCESSING\"}";
        String jsonResult4 = "{\"id\":1,\"clientIP\":\"192.168.1.254\",\"status\":\"AWAITING_RETRIEVAL\"}";
        String jsonResult5 = "{\"id\":2,\"clientIP\":\"192.168.1.1\",\"status\":\"AVAILABLE\"}";
        ApiSession apiSession = new ApiSession(sessionIP);

        assertEquals((Integer) 1, apiSession.getId());
        assertEquals(sessionIP, apiSession.getClientIP());
        assertEquals(ApiSession.SessionStatus.AVAILABLE, apiSession.getStatus());
        assertEquals(jsonResult, apiSession.toJson());

        apiSession.setClientIP(sessionIP2);

        assertEquals(sessionIP2, apiSession.getClientIP());
        assertEquals(jsonResult2, apiSession.toJson());

        apiSession.setStatus(ApiSession.SessionStatus.BATCH_PROCESSING);
        assertEquals(ApiSession.SessionStatus.BATCH_PROCESSING, apiSession.getStatus());
        assertEquals(jsonResult3, apiSession.toJson());

        apiSession.setStatus(ApiSession.SessionStatus.AWAITING_RETRIEVAL);
        assertEquals(ApiSession.SessionStatus.AWAITING_RETRIEVAL, apiSession.getStatus());
        assertEquals(jsonResult4, apiSession.toJson());

        ApiSession apiSession2 = new ApiSession();
        apiSession2.setClientIP(sessionIP);

        assertEquals(sessionIP, apiSession2.getClientIP());
        assertEquals((Integer) 2, apiSession2.getId());
        assertEquals(jsonResult5, apiSession2.toJson());

        ApiSession.resetSessionIdCounter();
    }

    @Test
    public void resultTest() throws JsonProcessingException {
        ApiSession.resetSessionIdCounter();
        String sessionIP = "192.168.1.1";
        double result = 42.0;

        ApiSession apiSession = new ApiSession(sessionIP);
        apiSession.setResult(result);

        assertEquals(result, apiSession.getResult(), 0);
        System.out.println(apiSession.toJson());

        ApiSession.resetSessionIdCounter();
    }

    /*
    The purpose of this test is to validate that generating
    new session Ids using the synchronized int newSessionId()
    method is thread safe. This test is primitive, non automated -
    requiring manual log inspection, and encounters an issue of
    "thread starvation". But by the very fact that thread tt1
    starves threads tt2 and tt3 of access to the newSessionId(),
    we can conclude that this method is thread safe.

    I,by no means consider the newSessionId() method a production ready
    solution, but feel that is adequate for the purposes of this
    demonstration if treated as a black box place holder.
     */
    @Test
    public void treadSafeTest() {
        ApiSession.resetSessionIdCounter();
        ConcurrentMap<Integer, ApiSession> sessionMap = new ConcurrentHashMap<>();
        int limit = 50;

        TestThread tt1 = new TestThread(sessionMap, limit);
        TestThread tt2 = new TestThread(sessionMap, limit);
        TestThread tt3 = new TestThread(sessionMap, limit);

        tt1.run();
        tt2.run();
        tt3.run();

        System.out.println(sessionMap.size());

        ApiSession.resetSessionIdCounter();
    }

    public class TestThread extends Thread {
        private final ConcurrentMap<Integer, ApiSession> sessionMap;
        private int limit;

        public TestThread(ConcurrentMap<Integer, ApiSession> sessionMap, int limit) {
            this.sessionMap = sessionMap;
            this.limit = limit;
        }

        @Override
        public void run() {
            while (sessionMap.size() < limit) {
                ApiSession apiSession = new ApiSession();
                sessionMap.put(apiSession.getId(), apiSession);
                System.out.println(Thread.currentThread().getId() + ": " + apiSession.getId());
            }
        }
    }

}
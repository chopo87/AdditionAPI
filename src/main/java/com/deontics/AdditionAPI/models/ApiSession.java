package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApiSession extends ApiAbstractModel {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)

    private static Integer sessionIdCounter = 0;

    private static synchronized int newSessionId() {
        return ++sessionIdCounter;
    }

    // For unit testing - Not great solution but pressed for time
    protected static synchronized void resetSessionIdCounter() {
        sessionIdCounter = 0;
    }

    /*
    It should be noted that in a production grade Spring
    model bean, the unique nature of the ID field would
    be ensured via some form of annotation such as:
    @Id or @GeneratedValue(strategy = ***)

    These annotations, however, are usually intended for use with
    a persistence data y model (e.g. @Entity) together with a
    repository system. As we established that this API was to
    avoid using a persistent data structure connected to a DB
    I judged that it would be faster to createSession a thread safe
    newSessionId() method in order to auto-generate unique IDs
    rather than to spend time looking for a non persistent
    production grade Spring compatible API. This approach has
    the added advantage of demonstrating some basic multi-threading
    principals.

    I have also later realised that this problem could also be resolved
    using a "static AtomicInteger counter = new AtomicInteger(1)", but
    at this point I decided it was still interesting to leave a basic
    threading example in this code this demo.

    Regardless, the  "synchronized int newSessionId()" method should
    be treated as a black box place holder and not as production
    grade code.
    */
    private Integer id;

    private String clientIP;

    private SessionStatus status;

    @JsonIgnore
    private double result;

    public ApiSession() {
        this.id = newSessionId();
        this.status = SessionStatus.AVAILABLE;
        this.result = 0;
    }

    public ApiSession(String clientIP) {
        this.id = newSessionId();
        this.status = SessionStatus.AVAILABLE;
        this.clientIP = clientIP;
        this.result = 0;
    }

    public Integer getId() {
        return id;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public enum SessionStatus {
        AVAILABLE,
        BATCH_PROCESSING,
        AWAITING_RETRIEVAL
    }
}

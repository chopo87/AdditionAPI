package com.deontics.AdditionAPI;

import com.deontics.AdditionAPI.models.ApiResultContainer;
import com.deontics.AdditionAPI.models.ApiSession;
import com.deontics.AdditionAPI.models.ApiTransferContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SpringBootApplication
public class AdditionApiApplication {

    @Bean
    public static final ConcurrentMap<String, ApiResultContainer> resultCacheMap() {
        ConcurrentMap<String, ApiResultContainer> resultCacheMap = new ConcurrentHashMap<>();
        return resultCacheMap;
    }

    @Bean
    public static final ConcurrentMap<Integer, ApiSession> sessionMap() {
        ConcurrentMap<Integer, ApiSession> sessionMap = new ConcurrentHashMap<>();
        return sessionMap;
    }

    @Bean
    public static final ConcurrentMap<Integer, ApiTransferContainer> batchResultMap() {
        ConcurrentMap<Integer, ApiTransferContainer> batchResultMap = new ConcurrentHashMap<>();
        return batchResultMap;
    }

    public static void main(String[] args) {
        SpringApplication.run(AdditionApiApplication.class, args);
    }
}

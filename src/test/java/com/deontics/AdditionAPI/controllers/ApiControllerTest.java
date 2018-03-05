package com.deontics.AdditionAPI.controllers;

import com.deontics.AdditionAPI.AdditionApiApplication;
import com.deontics.AdditionAPI.models.*;
import com.deontics.AdditionAPI.services.AdditionEngineApiService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ContextConfiguration(classes = AdditionApiApplication.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ApiControllerTest {

    private volatile MockMvc mockMvc;

    @Autowired
    private volatile WebApplicationContext webApplicationContext;

    @Before
    public void mockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/AdditionEngine")).andReturn();

        assertEquals( 200, mvcResult.getResponse().getStatus());

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiSession session = (ApiSession) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiSession.class);

        System.out.println(session.toJson());

    }


    @Test
    public void postTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/AdditionEngine")).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiSession session = (ApiSession) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiSession.class);

        List<Double> mySumList = new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81, 1.618));
        Double expectedResult = 140.628;

        ApiRequestArray ara = new ApiRequestArray(mySumList);
        ApiTransferContainer atc = new ApiTransferContainer(session, ara);

        mvcResult = this.mockMvc.perform(
                post("/AdditionEngine")
                        .content(atc.toJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        JsonNode jsonNode = ApiAbstractModel.getObjectMapper().readTree(jsonResponse);
        assertEquals(expectedResult, jsonNode.get("data").get(0).get("result").asDouble(), 0);

        assert true;
    }

    @Test
    public void batchPostTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/AdditionEngine")).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiSession session = (ApiSession) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiSession.class);

        ApiRequestArray ara = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81)));
        ApiRequestArray ara2 = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81, 1.618)));
        ApiRequestArray ara3 = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 6.67, 2.99)));

        List<ApiAbstractTransferModel> aral = new ArrayList<>(Arrays.asList(ara, ara2, ara3));
        ApiTransferContainer atc = new ApiTransferContainer(session, aral);

        mvcResult = this.mockMvc.perform(
                post("/AdditionEngine")
                        .content(atc.toJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);
    }

    @Test
    public void batchRetrialTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get("/AdditionEngine/99")).andReturn();
        assertEquals( 400, mvcResult.getResponse().getStatus());

        mvcResult = this.mockMvc.perform(get("/AdditionEngine")).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiSession session = (ApiSession) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiSession.class);

        mvcResult = this.mockMvc.perform(get("/AdditionEngine/" + session.getId() )).andReturn();
        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiTransferContainer atcResult = (ApiTransferContainer) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiTransferContainer.class);
        assertEquals(ApiSession.SessionStatus.AVAILABLE, atcResult.getSession().getStatus());

        ApiRequestArray ara = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81)));
        ApiRequestArray ara2 = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 42.0, 9.81, 1.618)));
        ApiRequestArray ara3 = new ApiRequestArray(new ArrayList<>(Arrays.asList(87.2, 6.67, 2.99)));

        List<ApiAbstractTransferModel> aral = new ArrayList<>(Arrays.asList(ara, ara2, ara3));
        ApiTransferContainer atc = new ApiTransferContainer(session, aral);

        mvcResult = this.mockMvc.perform(
                post("/AdditionEngine")
                        .content(atc.toJson())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        atcResult = (ApiTransferContainer) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiTransferContainer.class);
        assertEquals(ApiSession.SessionStatus.AWAITING_RETRIEVAL, atcResult.getSession().getStatus());
        System.out.println(jsonResponse);

        mvcResult = this.mockMvc.perform(get("/AdditionEngine/" + session.getId() )).andReturn();
        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        atcResult = (ApiTransferContainer) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiTransferContainer.class);
        assertEquals(ApiSession.SessionStatus.AVAILABLE, atcResult.getSession().getStatus());
    }

    @Test
    public void deleteTest() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(delete("/AdditionEngine/99")).andReturn();
        assertEquals( 400, mvcResult.getResponse().getStatus());

        mvcResult = this.mockMvc.perform(get("/AdditionEngine")).andReturn();
        assertEquals( 200, mvcResult.getResponse().getStatus());
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        ApiSession session = (ApiSession) ApiAbstractModel.getObjectMapper().readValue(jsonResponse, ApiSession.class);

        mvcResult = this.mockMvc.perform(delete("/AdditionEngine/" + session.getId() )).andReturn();
        assertEquals( 200, mvcResult.getResponse().getStatus());
        jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);
    }
}
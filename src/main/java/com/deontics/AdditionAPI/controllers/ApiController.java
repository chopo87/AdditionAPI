package com.deontics.AdditionAPI.controllers;

import com.deontics.AdditionAPI.models.*;
import com.deontics.AdditionAPI.services.AdditionEngineApiService;
import com.deontics.AdditionAPI.services.IllegalApiCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@RequestMapping("/AdditionEngine")
@RestController
public class ApiController {

    @Autowired
    private ConcurrentMap<String, ApiResultContainer> resultCacheMap;
    @Autowired
    private ConcurrentMap<Integer, ApiSession> sessionMap;
    @Autowired
    private ConcurrentMap<Integer, ApiTransferContainer> batchResultMap;

    @Autowired
    private AdditionEngineApiService additionService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    ApiSession getRequest(HttpServletRequest request) throws Exception {

        return additionService.createSession(request.getRemoteAddr());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    ApiTransferContainer getStatusRequest(@PathVariable Integer sessionId) throws Exception {

        if (sessionMap.containsKey(sessionId)) {
            ApiSession session = sessionMap.get(sessionId);

            if (session.getStatus() == ApiSession.SessionStatus.AWAITING_RETRIEVAL) {
                session.setStatus(ApiSession.SessionStatus.AVAILABLE);
                sessionMap.replace(sessionId, session);
                return batchResultMap.get(sessionId);
            } else return new ApiTransferContainer(
                    session,
                    new ApiMessageContainer("STATUS: " + session.getStatus())
            );
        } else
            throw new IllegalApiCallException("Error: Session is invalid or has expired, please request new session");
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public @ResponseBody
    ApiTransferContainer postRequest(@RequestBody ApiTransferContainer atc) throws Exception {

        System.out.println(atc.toJson());
        ApiSession session = sessionMap.get(atc.getSession().getId());
        List<ApiAbstractTransferModel> atmlList = new ArrayList<>(atc.getData());

        if (atc.getTransmissionType() == ApiTransferContainer.TransmissionType.SINGLE) {
            ApiAbstractTransferModel aatm = atmlList.get(0);
            if (aatm.getClass() == ApiRequestArray.class)
                return additionService.calculate((ApiRequestArray) aatm, session);
            else
                throw new IllegalApiCallException("Invalid 'data' node within transmission");
        } else if (atc.getTransmissionType() == ApiTransferContainer.TransmissionType.BATCH) {
            List<ApiRequestArray> araList = atmlList
                    .stream()
                    .filter(ApiRequestArray.class::isInstance)
                    .map(ApiRequestArray.class::cast)
                    .collect(Collectors.toList());
            if (atmlList.size() != araList.size())
                throw new IllegalApiCallException(
                        (atmlList.size() - araList.size()) +
                                "invalid 'data' nodes found within transmission");

            System.out.println(session.getStatus());
            additionService.batchCalculate(araList, session.getId());
            System.out.println(session.getStatus());

            ApiMessageContainer amc = new ApiMessageContainer("STATUS: Batch request achnowledged - Being processed");
            ApiTransferContainer atcResponse = new ApiTransferContainer(session, amc);
            atcResponse.setTransmissionType(ApiTransferContainer.TransmissionType.BATCH);
            return atcResponse;
        }

        throw new IllegalApiCallException("Session termination not yet implemented");
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    void delete(@PathVariable Integer sessionId) throws IllegalApiCallException {
        if (sessionMap.containsKey(sessionId)) {
            additionService.drop(sessionId);
        } else
            throw new IllegalApiCallException("Error: Session is invalid or has expired, no deletion necessary");
    }
}

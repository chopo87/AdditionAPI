package com.deontics.AdditionAPI.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiTransferContainer extends ApiAbstractModel {

    private ApiSession session;

    private List<ApiAbstractTransferModel> data;

    private TransmissionType transmissionType;

    //For use by Jackson in JSON deserialization
    public ApiTransferContainer() {
        this.data = new ArrayList<>();
    }

    public ApiTransferContainer(ApiSession session, ApiAbstractTransferModel dataObject) {
        this.session = session;
        this.data = new ArrayList<>(Arrays.asList(dataObject));
        this.transmissionType = TransmissionType.SINGLE;
    }

    public ApiTransferContainer(ApiSession session, List<ApiAbstractTransferModel> dataList) {
        this.session = session;
        this.data = new ArrayList<>(dataList);
        this.transmissionType = (data.size() > 1) ? TransmissionType.BATCH : TransmissionType.SINGLE;
    }

    public ApiSession getSession() {
        return session;
    }

    public void setSession(ApiSession session) {
        this.session = session;
    }

    public List<ApiAbstractTransferModel> getData() {
        return data;
    }

    public void setData(List<ApiAbstractTransferModel> data) {
        this.data = new ArrayList<>(data);
        this.transmissionType = (data.size() > 1) ? TransmissionType.BATCH : TransmissionType.SINGLE;

    }

    public void addData(ApiAbstractTransferModel dataElement) {
        this.data.add(dataElement);
        this.transmissionType = (data.size() > 1) ? TransmissionType.BATCH : TransmissionType.SINGLE;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public enum TransmissionType {
        SINGLE,
        BATCH
    }
}

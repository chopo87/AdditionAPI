package com.deontics.AdditionAPI.models;

public class ApiResultContainer extends ApiAbstractTransferModel {

    private double result;

    public ApiResultContainer() {
    }

    public ApiResultContainer(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}

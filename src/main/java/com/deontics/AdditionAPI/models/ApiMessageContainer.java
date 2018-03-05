package com.deontics.AdditionAPI.models;

public class ApiMessageContainer extends ApiAbstractTransferModel {

    private String message;

    public ApiMessageContainer() {
    }

    public ApiMessageContainer(String Message) {
        this.message = Message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

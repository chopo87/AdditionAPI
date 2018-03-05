package com.deontics.AdditionAPI.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ApiAbstractTransferModel extends ApiAbstractModel {
}

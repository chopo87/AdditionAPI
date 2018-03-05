package com.deontics.AdditionAPI.models;

import java.util.ArrayList;
import java.util.List;

public class ApiRequestArray extends ApiAbstractTransferModel {

    private List<Double> sumList;

    //For use by Jackson in JSON deserialization
    public ApiRequestArray() {
        this.sumList = new ArrayList<>();
    }

    public ApiRequestArray(List<Double> sumList) {
        this.sumList = new ArrayList<>(sumList);
    }

    public List<Double> getSumList() {
        return sumList;
    }

    public void setSumList(List<Double> sumList) {
        this.sumList = new ArrayList<>(sumList);
    }

    public void addToSumList(Double val) {
        this.sumList.add(val);
    }
}

package com.acfm.ble_beacon.entity;

public class ADStructure {
    Integer length;
    String type;
    String data;

    public ADStructure(Integer length, String type, String data) {
        this.length = length;
        this.type = type;
        this.data = data;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

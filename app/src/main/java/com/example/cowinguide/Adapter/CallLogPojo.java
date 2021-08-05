package com.example.cowinguide.Adapter;

import java.io.Serializable;

public class CallLogPojo implements Serializable {

    String number;
    String duaration;
    String type;
    String date;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDuaration() {
        return duaration;
    }

    public void setDuaration(String duaration) {
        this.duaration = duaration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

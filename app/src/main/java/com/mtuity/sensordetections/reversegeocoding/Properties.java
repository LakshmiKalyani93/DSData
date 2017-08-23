package com.mtuity.sensordetections.reversegeocoding;

import com.google.gson.annotations.SerializedName;

public class Properties {
    @SerializedName("short_code")
    private String shortCode;

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @Override
    public String toString() {
        return "ClassPojo [short_code = " + shortCode + "]";
    }

}


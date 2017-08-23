package com.mtuity.sensordetections.reversegeocoding;

import com.google.gson.annotations.SerializedName;

public class Context {
    @SerializedName("id")
    private String idContext;

    @SerializedName("text")
    private String textContext;

    public String getId() {
        return idContext;
    }

    public void setId(String id) {
        this.idContext = id;
    }

    public String getText() {
        return textContext;
    }

    public void setText(String text) {
        this.textContext = text;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + idContext + ", text = " + textContext + "]";
    }
}


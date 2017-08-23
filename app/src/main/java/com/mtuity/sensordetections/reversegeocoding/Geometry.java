package com.mtuity.sensordetections.reversegeocoding;

import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("type")
    private String typeGeo;

    @SerializedName("coordinates")
    private String[] coordinatesGeo;

    public String getType() {
        return typeGeo;
    }

    public void setType(String type) {
        this.typeGeo = type;
    }

    public String[] getCoordinates() {
        return coordinatesGeo;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinatesGeo = coordinates;
    }

    @Override
    public String toString() {
        return "ClassPojo [type = " + typeGeo + ", coordinates = " + coordinatesGeo + "]";
    }
}


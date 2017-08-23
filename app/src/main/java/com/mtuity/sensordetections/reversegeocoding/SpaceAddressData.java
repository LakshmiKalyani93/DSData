package com.mtuity.sensordetections.reversegeocoding;

import com.google.gson.annotations.SerializedName;

public class SpaceAddressData {
    @SerializedName("query")
    private String[] querySpaceAddressData;

    @SerializedName("features")
    private Features[] featuresSpaceAddressData;

    @SerializedName("type")
    private String typeSpaceAddressData;

    @SerializedName("attribution")
    private String attributionSpaceAddressData;

    public String[] getQuery() {
        return querySpaceAddressData;
    }

    public void setQuery(String[] query) {
        this.querySpaceAddressData = query;
    }

    public Features[] getFeatures() {
        return featuresSpaceAddressData;
    }

    public void setFeatures(Features[] features) {
        this.featuresSpaceAddressData = features;
    }

    public String getType() {
        return typeSpaceAddressData;
    }

    public void setType(String type) {
        this.typeSpaceAddressData = type;
    }

    public String getAttribution() {
        return attributionSpaceAddressData;
    }

    public void setAttribution(String attribution) {
        this.attributionSpaceAddressData = attribution;
    }

    @Override
    public String toString() {
        return "ClassPojo [query = " + querySpaceAddressData + ", features = " + featuresSpaceAddressData
                + ", type = " + typeSpaceAddressData + ", attribution = " + attributionSpaceAddressData + "]";
    }
}

package com.mtuity.sensordetections.reversegeocoding;

import com.google.gson.annotations.SerializedName;

public class Features {
    @SerializedName("center")
    private String[] centerFeatures;

    @SerializedName("id")
    private String idFeatures;

    @SerializedName("text")
    private String textFeatures;

    @SerializedName("place_name")
    private String placeNameFeatures;

    @SerializedName("context")
    private Context[] contextFeatures;

    @SerializedName("relevance")
    private String relevanceFeatures;

    @SerializedName("properties")
    private Properties propertiesFeatures;

    @SerializedName("type")
    private String typeFeatures;

    @SerializedName("geometry")
    private Geometry geometryFeatures;

    public String[] getCenter() {
        return centerFeatures;
    }

    public void setCenter(String[] center) {
        this.centerFeatures = center;
    }

    public String getId() {
        return idFeatures;
    }

    public void setId(String id) {
        this.idFeatures = id;
    }

    public String getText() {
        return textFeatures;
    }

    public void setText(String text) {
        this.textFeatures = text;
    }


    public Context[] getContext() {
        return contextFeatures;
    }

    public void setContext(Context[] context) {
        this.contextFeatures = context;
    }

    public String getRelevance() {
        return relevanceFeatures;
    }

    public void setRelevance(String relevance) {
        this.relevanceFeatures = relevance;
    }

    public Properties getProperties() {
        return propertiesFeatures;
    }

    public void setProperties(Properties properties) {
        this.propertiesFeatures = properties;
    }

    public String getType() {
        return typeFeatures;
    }

    public void setType(String type) {
        this.typeFeatures = type;
    }

    public Geometry getGeometry() {
        return geometryFeatures;
    }

    public void setGeometry(Geometry geometry) {
        this.geometryFeatures = geometry;
    }

    public String getPlaceName() {
        return placeNameFeatures;
    }

    public void setPlaceName(String placeName) {
        this.placeNameFeatures = placeName;
    }

    @Override
    public String toString() {
        return "ClassPojo [center = " + centerFeatures + ", id = " + idFeatures + ", text = " + textFeatures +
                ", place_name = " + placeNameFeatures + ", context = " + contextFeatures +
                ", relevance = " + relevanceFeatures + ", properties = " + propertiesFeatures + ", type = " + typeFeatures
                + ", geometry = " + geometryFeatures + "]";
    }
}
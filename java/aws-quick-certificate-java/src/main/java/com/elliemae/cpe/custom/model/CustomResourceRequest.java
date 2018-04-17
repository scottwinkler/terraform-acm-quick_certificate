package com.elliemae.cpe.custom.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
/*
 * Created by swinkler on 10/23/17.
 */
public class CustomResourceRequest {

    /* Create, Update, Delete */
    @SerializedName("RequestType")
    private String requestType;

    @SerializedName("ResourceType")
    private String resourceType;

    @SerializedName("ResourceProperties")
    private Map<String,String> resourceProperties;

    @SerializedName("OldResourceProperties")
    private Map<String,String> oldResourceProperties;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Map<String,String> getResourceProperties() {
        return resourceProperties;
    }

    public void setResourceProperties(Map<String,String> resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    public Map<String,String> getOldResourceProperties() {
        return oldResourceProperties;
    }

    public void setOldResourceProperties(Map<String,String> oldResourceProperties) {
        this.oldResourceProperties = oldResourceProperties;
    }
}

package com.elliemae.cpe.custom.model;

/**
 * Created by swinkler on 10/25/17.
 */

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * A Response to CloudFormation.
 * @see <a href=http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/crpg-ref-responses.html>CloudFormation Response Reference</a>
 */
public class CustomResourceResponse {

    public CustomResourceResponse(CustomResourceRequest request) {
        this.status = "SUCCESS";
    }

    @SerializedName("Status")
    private String status;

    @SerializedName("Reason")
    public String reason;

    @SerializedName("Data")
    private Map<String,String> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Map<String,String> getData() {
        return data;
    }

    public void setData(Map<String,String> data) {
        this.data = data;
    }
}
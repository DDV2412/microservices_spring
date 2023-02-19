package com.ipmugo.articleservice.dto;


import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "message-type",
        "message-version",
        "message"
})
@Generated("jsonschema2pojo")
public class CitationCrossRef {

    @JsonProperty("status")
    private String status;
    @JsonProperty("message-type")
    private String messageType;
    @JsonProperty("message-version")
    private String messageVersion;
    @JsonProperty("message")
    private EntryCrossRef message;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("message-type")
    public String getMessageType() {
        return messageType;
    }

    @JsonProperty("message-type")
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @JsonProperty("message-version")
    public String getMessageVersion() {
        return messageVersion;
    }

    @JsonProperty("message-version")
    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

    @JsonProperty("message")
    public EntryCrossRef getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(EntryCrossRef message) {
        this.message = message;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

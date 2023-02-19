package com.ipmugo.articleservice.dto;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "citedby-count",
})
@Generated("jsonschema2pojo")
public class EntryScopus {

    @JsonProperty("citedby-count")
    private String citedbyCount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("citedby-count")
    public String getCitedbyCount() {
        return citedbyCount;
    }

    @JsonProperty("citedby-count")
    public void setCitedbyCount(String citedbyCount) {
        this.citedbyCount = citedbyCount;
    }

}
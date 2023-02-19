package com.ipmugo.articleservice.dto;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "is-referenced-by-count",
})
@Generated("jsonschema2pojo")
public class EntryCrossRef {

    @JsonProperty("subject")
    private List<String> subject;

    @JsonProperty("is-referenced-by-count")
    private Integer referencesCount;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("is-referenced-by-count")
    public Integer getReferencesCount() {
        return referencesCount;
    }

    @JsonProperty("is-referenced-by-count")
    public void setReferencesCount(Integer referencesCount) {
        this.referencesCount = referencesCount;
    }

    @JsonProperty("subject")
    public List<String> getSubject() {
        return subject;
    }

    @JsonProperty("subject")
    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

}

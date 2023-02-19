package com.ipmugo.articleservice.dto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "opensearch:totalResults",
        "opensearch:startIndex",
        "opensearch:itemsPerPage",
        "opensearch:Query",
        "entry"
})
@Generated("jsonschema2pojo")
public class SearchResults {
    @JsonProperty("opensearch:totalResults")
    private String opensearchTotalResults;
    @JsonProperty("opensearch:startIndex")
    private String opensearchStartIndex;
    @JsonProperty("opensearch:itemsPerPage")
    private String opensearchItemsPerPage;
    @JsonProperty("entry")
    private List<EntryScopus> entry = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("opensearch:totalResults")
    public String getOpensearchTotalResults() {
        return opensearchTotalResults;
    }

    @JsonProperty("opensearch:totalResults")
    public void setOpensearchTotalResults(String opensearchTotalResults) {
        this.opensearchTotalResults = opensearchTotalResults;
    }

    @JsonProperty("opensearch:startIndex")
    public String getOpensearchStartIndex() {
        return opensearchStartIndex;
    }

    @JsonProperty("opensearch:startIndex")
    public void setOpensearchStartIndex(String opensearchStartIndex) {
        this.opensearchStartIndex = opensearchStartIndex;
    }

    @JsonProperty("opensearch:itemsPerPage")
    public String getOpensearchItemsPerPage() {
        return opensearchItemsPerPage;
    }

    @JsonProperty("opensearch:itemsPerPage")
    public void setOpensearchItemsPerPage(String opensearchItemsPerPage) {
        this.opensearchItemsPerPage = opensearchItemsPerPage;
    }

    @JsonProperty("entry")
    public List<EntryScopus> getEntry() {
        return entry;
    }

    @JsonProperty("entry")
    public void setEntry(List<EntryScopus> entry) {
        this.entry = entry;
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


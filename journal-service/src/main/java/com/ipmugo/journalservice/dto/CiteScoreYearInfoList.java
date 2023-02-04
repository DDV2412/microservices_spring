package com.ipmugo.journalservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "citeScoreCurrentMetric",
        "citeScoreCurrentMetricYear",
        "citeScoreTracker",
        "citeScoreTrackerYear"
})
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CiteScoreYearInfoList {

    @JsonProperty("citeScoreCurrentMetric")
    private String citeScoreCurrentMetric;
    @JsonProperty("citeScoreCurrentMetricYear")
    private String citeScoreCurrentMetricYear;
    @JsonProperty("citeScoreTracker")
    private String citeScoreTracker;
    @JsonProperty("citeScoreTrackerYear")
    private String citeScoreTrackerYear;

}

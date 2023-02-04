package com.ipmugo.journalservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SNIP"
})
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SNIPList {

    @JsonProperty("SNIP")
    private List<Snip> snip = null;
}

package com.ipmugo.journalservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "citationReport")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CitationReport {

    @Id
    private String id;

    private Double sjr;

    private Double snip;

    private Double citeScoreCurrent;

    private Double citeScoreTracker;

    private String currentYear;

    private String trackerYear;
}

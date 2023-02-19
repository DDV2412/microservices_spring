package com.ipmugo.journalservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "citationReport")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class CitationReport {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "journalId", nullable = false)
    @JsonBackReference
    private Journal journal;
    @Column
    private Double sjr;
    @Column
    private Double snip;
    @Column
    private Double citeScoreCurrent;
    @Column
    private Double citeScoreTracker;
    @Column
    private String currentYear;
    @Column
    private String trackerYear;
}

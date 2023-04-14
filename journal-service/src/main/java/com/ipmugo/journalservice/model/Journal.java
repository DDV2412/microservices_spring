package com.ipmugo.journalservice.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

@Table(name = "journal")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Journal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String name;

    @Column(unique = true)
    private String issn;

    @Column(unique = true)
    private String e_issn;

    @Column(unique = true)
    private String abbreviation;


    @Column
    private String pageImage;
    @Column
    private String coverImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String publisher;

    @Column(unique = true)
    private String journalSite;

    @Column
    private String country;

    @Column
    private String focusScope;

    @Column
    private String authorGuidelines;

    @Column
    private String privacyStatement;

    @Column
    private String license;

    @Column
    private BigDecimal authorFees;

    @Column
    private String reviewPolice;

    @CreationTimestamp
    private Timestamp updatedAt;

    @UpdateTimestamp
    private Timestamp createdAt;

    @ManyToMany(mappedBy = "journals", fetch = FetchType.EAGER)
    private Set<Category> categoriesList;

    @OneToOne(mappedBy = "journal", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CitationReport journalCitationReport;

}

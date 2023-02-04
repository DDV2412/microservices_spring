package com.ipmugo.journalservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "journal")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Journal {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    @Indexed(unique = true, name = "issn")
    private String issn;

    @Field
    @Indexed(unique = true, name = "e_issn")
    private String e_issn;

    @Field
    @Indexed(unique = true, name = "abbreviation")
    private String abbreviation;

    @Field
    private String thumbnail;

    @Field
    private String description;

    @Field
    private String publisher;

    @Field
    @Indexed(unique = true, name="journalSite")
    private String journalSite;

    @Field
    private String country;

    @Field
    private String focusScope;

    @Field
    private String authorGuidelines;

    @Field
    private String privacyStatement;

    @Field
    private String license;

    @Field
    private BigDecimal authorFees;

    @Field
    private String reviewPolice;

    @CreatedDate
    private LocalDateTime updatedAt;

    @LastModifiedDate
    private LocalDateTime createdAt;

    @DBRef
    private Set<Category> categoriesList;

    @DBRef
    private CitationReport journalCitationReport;

}

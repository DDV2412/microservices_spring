package com.ipmugo.articleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "article")
public class Article {

    @Id
    private String id;

    @DBRef
    private Journal journal;

    @Field
    private String ojsId;

    @Field
    private String title;

    @Field
    private String pages;

    @Field
    private String publishYear;

    @Field
    private String lastModifier;

    @Field
    private String publishDate;

    @Field
    private String doi;

    @Field
    private String volume;

    @Field
    private String issue;

    @Field
    private String publishStatus;

    @Field
    private String copyright;

    @Field
    private String abstractText;

    @Field
    private String fullText;

    @Field
    private String articlePdf;

    @Field
    private List<Keyword> keywords;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @DBRef
    private Set<Author> authors;

    @Field
    private int citationByScopus;

    @Field
    private int citationByCrossRef;

    @Field
    private List<String> figures;

    @Field
    private long viewsCount;

    @Field
    private long downloadCount;
}

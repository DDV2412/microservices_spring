package com.ipmugo.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "article")
public class Article {

    @Id
    private String id;
    @Field(type = FieldType.Object)
    private Journal journal;
    @Field(type = FieldType.Text, analyzer = "english")
    private String title;

    @Field(type = FieldType.Keyword)
    private String pages;

    @Field(type = FieldType.Keyword)
    private String publishYear;

    @Field(type = FieldType.Keyword)
    private String lastModifier;

    @Field(type = FieldType.Keyword)
    private String publishDate;

    @Field(type = FieldType.Keyword)
    private String doi;

    @Field(type = FieldType.Keyword)
    private String volume;

    @Field(type = FieldType.Keyword)
    private String issue;

    @Field(type = FieldType.Keyword)
    private String publishStatus;

    @Field(type = FieldType.Text, analyzer = "english")
    private String abstractText;

    @Field(type = FieldType.Text, analyzer = "english")
    private List<HashMap<String, String>> keywords;
    @Field(type = FieldType.Nested)
    private Set<Author> authors;

    @Field(type = FieldType.Object)
    private CitationScopus citationByScopus;

    @Field(type = FieldType.Object)
    private CitationCrossRef citationByCrossRef;

}






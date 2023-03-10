package com.ipmugo.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = Article.INDEX)
public class Article {

    public static final String INDEX = "article";


    @Id
    private String id;


    @Field(type = FieldType.Object, includeInParent = true)
    private Journal journal;

    @Field(type = FieldType.Keyword)
    private String ojsId;

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


    @Field(type = FieldType.Text)
    private String abstractText;


    @Field(type = FieldType.Keyword)
    private String articlePdf;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<Keyword> keywords;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Set<Author> authors;
    @Field(type = FieldType.Keyword)
    private int citationByScopus;
    @Field(type = FieldType.Keyword)
    private int citationByCrossRef;
    @Field(type = FieldType.Keyword)
    private List<String> figures;
    @Field(type = FieldType.Keyword)
    private long viewsCount;
    @Field(type = FieldType.Keyword)
    private long downloadCount;

}






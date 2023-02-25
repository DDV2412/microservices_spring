package com.ipmugo.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = Journal.INDEX)
public class Journal {

    public static final String INDEX = "journal";

    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String issn;
    @Field(type = FieldType.Keyword)
    private String e_issn;
    @Field(type = FieldType.Keyword)
    private String abbreviation;
    @Field(type = FieldType.Keyword)
    private String publisher;
    @Field(type = FieldType.Keyword)
    private String journalSite;
    @Field(type = FieldType.Keyword)
    private boolean scopusIndex;
}

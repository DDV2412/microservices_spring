package com.ipmugo.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "journal")
public class Journal {

    @Field(type = FieldType.Keyword)
    private UUID id;
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword)
    private String issn;
    @Field(type = FieldType.Keyword)
    private String e_issn;
    @Field(type = FieldType.Keyword)
    private String publisher;


}

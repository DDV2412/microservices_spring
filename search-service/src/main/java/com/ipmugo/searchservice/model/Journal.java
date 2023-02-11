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
@Document(indexName = "journal")
public class Journal {

    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "english")
    private String name;

    @Field(type = FieldType.Keyword)
    private String issn;

    @Field(type = FieldType.Keyword)
    private String e_issn;

    @Field(type = FieldType.Keyword)
    private String publisher;


}

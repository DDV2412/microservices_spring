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
@Document(indexName = Author.INDEX)
public class Author {

    public static final String INDEX = "author";

    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String firstName;

    @Field(type = FieldType.Keyword)
    private  String lastName;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
    private String affiliation;

    @Field(type = FieldType.Text, analyzer = "english")
    private String biography;

    @Field(type = FieldType.Keyword)
    private String orcid;

}

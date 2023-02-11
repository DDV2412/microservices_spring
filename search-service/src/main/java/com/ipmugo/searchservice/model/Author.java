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
public class Author {


    @Id
    private String id;


    @Field(type = FieldType.Keyword)
    private String firstName;


    @Field(type = FieldType.Keyword)
    private  String lastName;


    @Field(type = FieldType.Text, analyzer = "english")
    private String affiliation;


}

package com.ipmugo.articleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "author")
public class Author {

    @Id
    private String id;

    @Field
    private String firstName;

    @Field
    private  String lastName;

    @Field
    private String email;

    @Field
    private String affiliation;


    @Field
    private String orcid;



}

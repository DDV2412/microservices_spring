package com.ipmugo.articleservice.event;

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
public class AuthorEvent {

    private String id;

    private String firstName;


    private  String lastName;


    private String email;


    private String affiliation;


    private String biography;


    private String orcid;


}

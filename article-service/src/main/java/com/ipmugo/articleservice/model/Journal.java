package com.ipmugo.articleservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "journal")
public class Journal {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    private String abbreviation;
    @Field
    @Indexed(unique = true, name = "issn")
    private String issn;

    @Field
    @Indexed(unique = true, name = "e_issn")
    private String e_issn;

    @Field
    private String publisher;

    @Field
    @Indexed(unique = true, name="journalSite")
    private String journalSite;

}

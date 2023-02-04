package com.ipmugo.userservice.model;

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
@Document(value = "authorScholarProfile")
public class AuthorScholarProfile {

    @Id
    private String id;

    @Field
    private Integer citations;

    @Field
    private Integer hIndex;

    @Field
    private Integer i10Index;
}

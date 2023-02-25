package com.ipmugo.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serial;
import java.io.Serializable;


@Document(indexName = Keyword.INDEX)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Keyword implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String INDEX = "keyword";
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;
}

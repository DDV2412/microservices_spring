package com.ipmugo.setcounter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;


@Data
@Document(value = "setCounter")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetCounter {

    @Id
    private String id;

    @Field
    private String doi;
    @Field
    private Status status;

    @CreatedDate
    private LocalDateTime createdAt;

}

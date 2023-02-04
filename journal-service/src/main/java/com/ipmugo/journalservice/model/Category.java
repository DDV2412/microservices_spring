package com.ipmugo.journalservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Document(value = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Category {

    @Id
    private String id;

    @Field
    @NotBlank(message = "Illustration must not be blank")
    @Size(max = 255, message = "Illustration should not exceed 255 characters")
    private String illustration;

    @Field
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    @Indexed(unique = true)
    private String name;
}

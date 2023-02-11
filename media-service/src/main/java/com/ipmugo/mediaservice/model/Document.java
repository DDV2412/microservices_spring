package com.ipmugo.mediaservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@org.springframework.data.mongodb.core.mapping.Document(value = "document")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Document {  @Id
private String id;

    @Field
    private String fileName;

    @Field
    private Long fileSize;

    @Field
    private String fileType;


    @Field
    private byte[] fileByte;

    @CreatedDate
    private LocalDateTime updatedAt;

    @LastModifiedDate
    private LocalDateTime createdAt;
}

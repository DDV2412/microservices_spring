package com.ipmugo.mediaservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "media")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Media {

    @Id
    private String id;

    @Field
    private String fileName;

    @Field
    private Long fileSize;

    @Field
    private String fileType;

    @Field
    private String filePath;

    @Field
    private byte[] fileByte;

    @CreatedDate
    private LocalDateTime updatedAt;

    @LastModifiedDate
    private LocalDateTime createdAt;

}

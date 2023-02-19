package com.ipmugo.mediaservice.model;




import java.sql.Timestamp;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;

@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String fileName;

    @Column
    private Long fileSize;

    @Column
    private String fileType;

    @Column
    private byte[] fileByte;

    @CreationTimestamp
    private Timestamp updatedAt;

    @UpdateTimestamp
    private Timestamp createdAt;

}


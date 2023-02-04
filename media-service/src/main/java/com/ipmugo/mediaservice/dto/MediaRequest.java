package com.ipmugo.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MediaRequest {

    private String fileName;

    private Long fileSize;

    private String fileType;

    private byte[] fileByte;

    private String filePath;
}

package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {

    private String from;

    private String[] to;

    private String[] cc;

    private String[] bcc;

    private String replyTo;

    private String subject;

    private String body;

    private Date sendSceduled;

    private String attachmentPath;

}

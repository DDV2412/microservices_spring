package com.ipmugo.mailservice.service;

import com.ipmugo.mailservice.dto.MailRequest;
import com.ipmugo.mailservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(MailRequest mailRequest) throws CustomException {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(mailRequest.getFrom());
            helper.setTo(mailRequest.getTo());
            helper.setSubject(mailRequest.getSubject());
            helper.setSentDate(mailRequest.getSendSceduled());
            helper.setText(mailRequest.getBody());

            if(!mailRequest.getReplyTo().isEmpty()){
                helper.setReplyTo(mailRequest.getReplyTo());
            }

            if(!mailRequest.getAttachmentPath().isEmpty()){
                FileSystemResource file
                        = new FileSystemResource(
                        new File(mailRequest.getAttachmentPath()));

                helper.addAttachment(
                        Objects.requireNonNull(file.getFilename()), file);
            }

            if(mailRequest.getCc().length > 0){
                helper.setCc(mailRequest.getCc());
            }

            if(mailRequest.getBcc().length > 0){
                helper.setBcc(mailRequest.getBcc());
            }

            javaMailSender.send(message);
        }catch (MessagingException e){
            logger.error(e.getMessage());
            throw  new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }


}

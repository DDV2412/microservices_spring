package com.ipmugo.mailservice.controller;

import com.ipmugo.mailservice.dto.MailRequest;
import com.ipmugo.mailservice.dto.ResponseData;
import com.ipmugo.mailservice.service.MailService;
import com.ipmugo.mailservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/send/mail")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping()
    public ResponseEntity<ResponseData<String>> sendMail(@RequestBody MailRequest mailRequest){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            mailService.sendMail(mailRequest);

            responseData.setStatus(true);
            responseData.getMessages().add("Mail sended successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

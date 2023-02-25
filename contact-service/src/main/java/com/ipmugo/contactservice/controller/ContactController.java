package com.ipmugo.contactservice.controller;


import com.ipmugo.contactservice.dto.ResponseData;
import com.ipmugo.contactservice.model.Contact;
import com.ipmugo.contactservice.service.ContactService;
import com.ipmugo.contactservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<ResponseData<Page<Contact>>> getAllContact(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size){
        ResponseData<Page<Contact>> responseData = new ResponseData<>();
        try{
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
            responseData.setStatus(true);
            responseData.setData(contactService.getAllContact(pageable));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteContact(@PathVariable("id") UUID id){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            contactService.deleteContact(id);
            responseData.setStatus(true);
            responseData.getMessages().add("Delete contact successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseData<Contact>> contact(@RequestBody Contact contact){
        ResponseData<Contact> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.getMessages().add("Message sent successfully");
            responseData.setData(contactService.contact(contact));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

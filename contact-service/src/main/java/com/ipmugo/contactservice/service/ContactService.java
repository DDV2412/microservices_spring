package com.ipmugo.contactservice.service;

import com.ipmugo.contactservice.model.Contact;
import com.ipmugo.contactservice.repository.ContactRepository;
import com.ipmugo.contactservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactService {


    @Autowired
    private ContactRepository contactRepository;

    public Contact contact(Contact contact) throws CustomException {
        try{
           return contactRepository.save(contact);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public Page<Contact> getAllContact(Pageable pageable) throws CustomException {
        try{
            return contactRepository.findAll(pageable);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void deleteContact(UUID id) throws CustomException {
        try{
            contactRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

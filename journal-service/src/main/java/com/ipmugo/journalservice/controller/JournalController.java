package com.ipmugo.journalservice.controller;

import java.util.List;

import javax.validation.Valid;

import com.ipmugo.journalservice.dto.ResponseData;
import com.ipmugo.journalservice.model.Category;
import com.ipmugo.journalservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import com.ipmugo.journalservice.dto.JournalRequest;
import com.ipmugo.journalservice.model.Journal;
import com.ipmugo.journalservice.service.JournalService;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
public class JournalController {

    @Autowired
    private JournalService journalService;

    /**
     * Save or Update Journal
     * */
    @PostMapping
    public ResponseEntity<ResponseData<Journal>> createOrUpdateJournal(@Valid @RequestBody JournalRequest journalRequest,
                                                              Errors errors) {
        ResponseData<Journal> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            responseData.setData(journalService.createOrUpdateJournal(journalRequest));
            responseData.setStatus(true);
            return ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get List Journal
     * */
    @GetMapping
    public ResponseEntity<ResponseData<List<Journal>>> getAllJournals() {
        ResponseData<List<Journal>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(journalService.getAllJournals());

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get Journal By ID
     * */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Journal>> getJournal(@PathVariable("id") String id) {
        ResponseData<Journal> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(journalService.getJournal(id));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Delete Journal By ID
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteJournal(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            journalService.deleteJournal(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Journal deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Assign All Categories to Journal By ID
     * */
    @PostMapping("/assign-category/{id}")
    public ResponseEntity<ResponseData<Journal>> assignCategory(@PathVariable("id") String id, @Valid @RequestBody List<Category> category) {

        ResponseData<Journal> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(journalService.assignCategory(id, category));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

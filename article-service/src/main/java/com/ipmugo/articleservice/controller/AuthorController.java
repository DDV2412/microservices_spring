package com.ipmugo.articleservice.controller;

import com.ipmugo.articleservice.dto.ResponseData;
import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.service.AuthorService;
import com.ipmugo.articleservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/article/author")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    /**
     * Update AuthorEvent
     * */
    @PutMapping("/id")
    public ResponseEntity<ResponseData<Author>> updateAuthor(@PathVariable("id") String id, @Valid @RequestBody Author author,
                                                               Errors errors) {
        ResponseData<Author> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{

            Author author1 = authorService.updateAuthor(id, author);

            if(author1 == null){
                responseData.getMessages().add("AuthorEvent with id "+id+" not found");
                responseData.setData(null);
                responseData.setStatus(false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseData);
            }
            responseData.setData(author1);
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
     * Delete AuthorEvent By ID
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteAuthor(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            authorService.deleteAuthor(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("AuthorEvent deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

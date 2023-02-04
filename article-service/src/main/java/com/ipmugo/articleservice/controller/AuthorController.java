package com.ipmugo.articleservice.controller;

import com.ipmugo.articleservice.dto.ResponseData;
import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.service.AuthorService;
import com.ipmugo.articleservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    /**
     * Update Author
     * */
    @PutMapping("/{firstName}/{lastName}")
    public ResponseEntity<ResponseData<Author>> updateAuthor(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName, @Valid @RequestBody Author author,
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

            Author author1 = authorService.updateAuthor(firstName, lastName, author);

            if(author1 == null){
                responseData.getMessages().add("Author with firstname "+firstName+" not found");
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
     * Find Author by Firstname and LastName
     * */
    @GetMapping("/{firstName}/{lastName}")
    public ResponseEntity<ResponseData<Author>> getAuthor(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
        ResponseData<Author> responseData = new ResponseData<>();

        try{

            Author author1 = authorService.getAuthor(firstName, lastName);

            if(author1 == null){
                responseData.getMessages().add("Author with firstname "+firstName+" not found");
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
     * Delete Author By ID
     * */
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseData<String>> deleteAuthor(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            authorService.deleteAuthor(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Author deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

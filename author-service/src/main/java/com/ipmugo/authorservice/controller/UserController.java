package com.ipmugo.authorservice.controller;

import com.ipmugo.authorservice.dto.AssignRequest;
import com.ipmugo.authorservice.dto.ResponseData;
import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.User;
import com.ipmugo.authorservice.service.UserService;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/assign")
    public ResponseEntity<ResponseData<String>> assignAuthor(@RequestBody AssignRequest data){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            userService.assignArticle(data.getUser(), data.getArticle());

            responseData.getMessages().add("Assigned author successfully");
            responseData.setStatus(true);

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/publications/{id}")
    public ResponseEntity<ResponseData<Iterable<Article>>> authorPublication(@PathVariable("id") String id){
        ResponseData<Iterable<Article>> responseData = new ResponseData<>();
        try{
            responseData.setData(userService.getPublications(id));
            responseData.setStatus(true);

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/featured-authors")
    public ResponseEntity<ResponseData<List<User>>> featuredAuthor(){
        ResponseData<List<User>> responseData = new ResponseData<>();
        try{
            responseData.setData(userService.featuredAuthor());
            responseData.setStatus(true);

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

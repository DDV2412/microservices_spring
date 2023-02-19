package com.ipmugo.authorservice.controller;

import com.ipmugo.authorservice.dto.AssignRequest;
import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.User;
import com.ipmugo.authorservice.service.UserService;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/assign")
    public ResponseEntity<String> assignAuthor(@RequestBody AssignRequest data){
        try{
            userService.assignArticle(data.getUser(), data.getArticle());

            return ResponseEntity.ok("Assigned author successfully");
        }catch (CustomException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/publications/{id}")
    public ResponseEntity<Iterable<Article>> authorPublication(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(userService.getPublications(id));
        }catch (CustomException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ArrayList<>());
        }
    }
}

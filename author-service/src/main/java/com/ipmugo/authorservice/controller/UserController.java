package com.ipmugo.authorservice.controller;

import com.ipmugo.authorservice.dto.ResponseData;
import com.ipmugo.authorservice.model.Publication;
import com.ipmugo.authorservice.model.User;
import com.ipmugo.authorservice.service.UserService;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/assign")
    public ResponseEntity<ResponseData<User>> assignAuthor(@RequestBody User data){
        ResponseData<User> responseData = new ResponseData<>();
        try{

            responseData.setStatus(true);
            responseData.setData(userService.assignArticle(data));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setStatus(false);

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/publications/{id}")
    public ResponseEntity<ResponseData<Iterable<Publication>>> authorPublication(@PathVariable("id") String id){
        ResponseData<Iterable<Publication>> responseData = new ResponseData<>();
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

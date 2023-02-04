package com.ipmugo.userservice.controller;

import com.ipmugo.userservice.dto.UserResponse;
import com.ipmugo.userservice.service.UserService;
import com.ipmugo.userservice.dto.ResponseData;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/management")
public class UserController {

    @Autowired
    private UserService userService;

    /***
     * Admin Create Account
     */
    @PostMapping("/user")
    public ResponseEntity<ResponseData<UserResponse>> createUser(@Valid @RequestBody User userData, Errors errors){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            User user = userService.createUser(userData);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user, ""));
            return  ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Update Account
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<ResponseData<UserResponse>> updateUser(@PathVariable("id") String userId, @Valid @RequestBody User userData, Errors errors){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            User user = userService.updateUser(userId, userData);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user, ""));
            return  ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Delete Account
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<ResponseData<UserResponse>> deleteAccount(@PathVariable("id") String id){
        ResponseData<UserResponse> responseData = new ResponseData<>();


        try{
            userService.deleteUser(id);

            responseData.setStatus(true);
            responseData.getMessages().add("Deleted account with id "+ id + " successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Multiple Delete Account
     */
    @DeleteMapping("/user")
    public ResponseEntity<ResponseData<UserResponse>> multipleDelete(@PathVariable("id") List<String> id){
        ResponseData<UserResponse> responseData = new ResponseData<>();


        try{
            userService.multipleDelete(id);

            responseData.setStatus(true);
            responseData.getMessages().add("Deleted account with ids "+ id + " successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Disable Account
     */

    @PatchMapping("/user/{id}")
    public ResponseEntity<ResponseData<UserResponse>> disableAccount(@PathVariable("id") String id){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        try{
            userService.disableAccount(id);

            responseData.setStatus(true);
            responseData.getMessages().add("Disabled account with id "+ id + " successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Get All Account
     */
    @GetMapping("/user")
    public ResponseEntity<ResponseData<Iterable<User>>> getAllUser(){
        ResponseData<Iterable<User>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(userService.getAllUser());
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Get by ID Account
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseData<UserResponse>> getUser(@PathVariable("id") String id){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        try{
            User user = userService.getUser(id);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user, ""));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /***
     * Admin Assign user role
     */
    @PostMapping("/assign-role/{id}")
    public ResponseEntity<ResponseData<UserResponse>> assignRole(@PathVariable("id") String userId,@RequestBody List<Role> roles){
        ResponseData<UserResponse> responseData = new ResponseData<>();
        try{
            User user= userService.assignRole(userId, roles);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user, ""));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

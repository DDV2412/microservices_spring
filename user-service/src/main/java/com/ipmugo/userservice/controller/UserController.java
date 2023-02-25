package com.ipmugo.userservice.controller;

import com.ipmugo.userservice.dto.UserResponse;
import com.ipmugo.userservice.service.UserService;
import com.ipmugo.userservice.dto.ResponseData;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
            responseData.setData(new UserResponse().getBuilder(user));
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
    public ResponseEntity<ResponseData<UserResponse>> updateUser(@PathVariable("id") UUID userId, @Valid @RequestBody User userData, Errors errors){
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
            responseData.setData(new UserResponse().getBuilder(user));
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
    public ResponseEntity<ResponseData<UserResponse>> deleteAccount(@PathVariable("id") UUID id){
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
    public ResponseEntity<ResponseData<UserResponse>> multipleDelete(@PathVariable("id") List<UUID> id){
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
    public ResponseEntity<ResponseData<UserResponse>> disableAccount(@PathVariable("id") UUID id){
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
    public ResponseEntity<ResponseData<Page<User>>> getAllUser(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size, @RequestParam(value = "search", required = false) String search){
        ResponseData<Page<User>> responseData = new ResponseData<>();

        try{
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));
            responseData.setStatus(true);
            responseData.setData(userService.getAllUser(pageable, search));
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
    public ResponseEntity<ResponseData<UserResponse>> getUser(@PathVariable("id") UUID id){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        try{
            User user = userService.getUser(id);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user));
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
    public ResponseEntity<ResponseData<UserResponse>> assignRole(@PathVariable("id") UUID userId,@RequestBody List<Role> roles){
        ResponseData<UserResponse> responseData = new ResponseData<>();
        try{
            User user= userService.assignRole(userId, roles);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(user));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}

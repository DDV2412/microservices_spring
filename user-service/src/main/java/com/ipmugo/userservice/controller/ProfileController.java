package com.ipmugo.userservice.controller;


import com.ipmugo.userservice.dto.PasswordRequest;
import com.ipmugo.userservice.dto.ProfileUpdate;
import com.ipmugo.userservice.dto.ResponseData;
import com.ipmugo.userservice.dto.UserResponse;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import com.ipmugo.userservice.utils.AuthTokenFilter;
import com.ipmugo.userservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    /**
     * Get Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ResponseData<UserResponse>> getProfile(@RequestHeader("accessToken") String token) {
        ResponseData<UserResponse> responseData = new ResponseData<>();

        try {
            /**
             * Parsing JWT Token
             * Bearer and Token
             * */
            String jwt = jwtUtils.parseJwt(token);

            /**
             * Checking token not null and token valid
             * */
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                responseData.setStatus(false);
                responseData.getMessages().add(String.format("Invalid JWT signature " + jwt));

                return ResponseEntity.internalServerError().body(responseData);
            }

            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            User profile = userService.getProfile(username);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(profile));

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
    }

    /**
     * Update Profile
     */
    @PutMapping("/profile/update")
    public ResponseEntity<ResponseData<UserResponse>> updateProfile(@Valid @RequestBody ProfileUpdate user, Errors errors, @RequestHeader("accessToken") String token) {
        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }
        try {
            /**
             * Parsing JWT Token
             * Bearer and Token
             * */
            String jwt = jwtUtils.parseJwt(token);

            /**
             * Checking token not null and token valid
             * */
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                responseData.setStatus(false);
                responseData.getMessages().add(String.format("Invalid JWT signature " + jwt));

                return ResponseEntity.internalServerError().body(responseData);
            }

            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            User updateUser = userService.updateProfile(user, username);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(updateUser));

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.internalServerError().body(responseData);
        }
    }

    /**
     * Update Password
     */
    @PutMapping("/password/update")
    public ResponseEntity<ResponseData<UserResponse>> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest, Errors errors, @RequestHeader("accessToken") String token) {
        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try {

            /**
             * Parsing JWT Token
             * Bearer and Token
             * */
            String jwt = jwtUtils.parseJwt(token);

            /**
             * Checking token not null and token valid
             * */
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                responseData.setStatus(false);
                responseData.getMessages().add(String.format("Invalid JWT signature " + jwt));

                return ResponseEntity.internalServerError().body(responseData);
            }

            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            User updateUser = userService.updatePassword(passwordRequest, username);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(updateUser));

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.internalServerError().body(responseData);
        }
    }

    /**
     * Delete Account
     */

    @DeleteMapping("/profile/delete")
    public ResponseEntity<ResponseData<UserResponse>> deleteAccount(@RequestHeader("accessToken") String token) {
        ResponseData<UserResponse> responseData = new ResponseData<>();


        try {
            /**
             * Parsing JWT Token
             * Bearer and Token
             * */
            String jwt = jwtUtils.parseJwt(token);

            /**
             * Checking token not null and token valid
             * */
            if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
                responseData.setStatus(false);
                responseData.getMessages().add(String.format("Invalid JWT signature " + jwt));

                return ResponseEntity.internalServerError().body(responseData);
            }

            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            User updateUser = userService.deleteAccount(username);

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(updateUser));

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            responseData.setStatus(false);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.internalServerError().body(responseData);
        }
    }
}
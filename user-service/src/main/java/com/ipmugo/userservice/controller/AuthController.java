package com.ipmugo.userservice.controller;

import com.ipmugo.userservice.dto.*;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.service.UserService;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;

import com.ipmugo.userservice.model.OneTimePassword;

import com.ipmugo.userservice.service.OneTimePasswordService;
import com.ipmugo.userservice.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    @Autowired
    private UserService userService;

    @Autowired
    private OneTimePasswordService oneTimePasswordService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * New User Registration
     * Generate OTP
     * */
    @PostMapping("/register")
    public ResponseEntity<ResponseData<OneTimePassword>> registerUser(@Valid @RequestBody VerifyRequest verifyRequest, Errors errors){
        ResponseData<OneTimePassword> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            OneTimePassword otp = oneTimePasswordService.generateOtp(verifyRequest);
            responseData.setStatus(true);
            responseData.setData(otp);
            return  ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * New User Registration
     * Generate OTP
     * */
    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseData<OneTimePassword>> verifyOtp(@Valid @RequestBody OtpRequest otpRequest, Errors errors){
        ResponseData<OneTimePassword> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            OneTimePassword otp = oneTimePasswordService.verifyOtp(otpRequest.getOtp());
            responseData.setStatus(true);
            responseData.setData(otp);
            return  ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Setup Account User
     * */
    @PostMapping("/setup-account")
    public ResponseEntity<ResponseData<UserResponse>> setupAccount(@Valid @RequestBody RegisterRequest registerRequest, Errors errors){
        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            User user = userService.registerUser(registerRequest);

            oneTimePasswordService.deleteOtp(registerRequest.getEmail());

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

    /**
     * User Authentication with Login
     * */
    @PostMapping("/login")
    public ResponseEntity<ResponseData<UserResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, Errors errors) {

        ResponseData<UserResponse> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User userDetails = (User) authentication.getPrincipal();

            responseData.setStatus(true);
            responseData.setData(new UserResponse().getBuilder(userDetails, jwt));

            return ResponseEntity.ok(responseData);
        }catch (Exception e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED).body(responseData);
        }
    }
}

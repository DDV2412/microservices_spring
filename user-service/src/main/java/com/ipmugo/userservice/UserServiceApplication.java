package com.ipmugo.userservice;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.RoleEnum;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.repository.RoleRepository;
import com.ipmugo.userservice.repository.UserRepository;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
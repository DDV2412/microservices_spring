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
@RequiredArgsConstructor
public class UserServiceApplication {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
    
    public void run(String[] args) {
        if (roleRepository.count() < 1) {
            List<Role> roleList = Arrays.asList(
                    Role.builder().name(RoleEnum.Administrator).build(),
                    Role.builder().name(RoleEnum.Reader).build()
            );

            roleRepository.saveAll(roleList);
        }

        Set<Role> roles = new HashSet<>();

        Role assignRole = roleRepository.findByName(RoleEnum.Administrator)
                .orElseThrow(() -> new CustomException("Role is not found.", HttpStatus.NOT_FOUND));

        roles.add(assignRole);

        if (userRepository.count() < 1) {
            User admin = User.builder()
                    .firstName("Dian")
                    .lastName("Dwi Vaputra")
                    .email("dhyanputra24@gmail.com")
                    .password(bCryptPasswordEncoder.encode("Dhyan2412#"))
                    .username("Dhyan Putra")
                    .isEnabled(true)
                    .isVerify(true)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
        }
    }
}
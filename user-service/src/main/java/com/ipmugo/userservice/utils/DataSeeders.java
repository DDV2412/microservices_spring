package com.ipmugo.userservice.utils;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.RoleEnum;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.repository.RoleRepository;
import com.ipmugo.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeders  implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void onApplicationEvent(ContextRefreshedEvent event) {
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

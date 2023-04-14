package com.ipmugo.userservice.utils;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.model.UserRole;
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
                    Role.builder().name(UserRole.Administrator).build(),
                    Role.builder().name(UserRole.Reader).build(),
                    Role.builder().name(UserRole.Author).build()
            );

            roleRepository.saveAll(roleList);
        }

        Role assignRole = roleRepository.findByName(UserRole.Administrator)
                .orElseThrow(() -> new CustomException("Role is not found.", HttpStatus.NOT_FOUND));


        if (userRepository.count() < 1) {
            User admin = User.builder()
                    .firstName("Dian")
                    .lastName("Dwi Vaputra")
                    .email("dhyanputra24@gmail.com")
                    .password(bCryptPasswordEncoder.encode("Dhyan2412#"))
                    .username("Dhyan Putra")
                    .isEnabled(true)
                    .isVerify(true)
                    .build();


            User save = userRepository.save(admin);

            assignRole.getUsers().add(save);

            roleRepository.save(assignRole);
        }
    }
}

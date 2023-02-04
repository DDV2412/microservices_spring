package com.ipmugo.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ipmugo.userservice.dto.PasswordRequest;
import com.ipmugo.userservice.dto.ProfileUpdate;
import com.ipmugo.userservice.dto.RegisterRequest;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.RoleEnum;
import com.ipmugo.userservice.model.User;
import com.ipmugo.userservice.repository.RoleRepository;
import com.ipmugo.userservice.repository.UserRepository;
import com.ipmugo.userservice.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Load User by Username
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with Username: " + username));

    }

    /**
     * Register User
     * */
    public User registerUser(RegisterRequest registerRequest) throws CustomException{
        try{
            Optional<Role> role = roleRepository.findByName(RoleEnum.Reader);

            if(userRepository.existsByEmail(registerRequest.getEmail()) || userRepository.existsByUsername(registerRequest.getUsername()) || role.isEmpty()){
                throw new CustomException(String.format("User with Email '%s' or Username '%s' not Available", registerRequest.getEmail(), registerRequest.getUsername()), HttpStatus.BAD_GATEWAY);
            }

            Set<Role> roles = new HashSet<>();

            Role assignRole = roleRepository.findByName(RoleEnum.Reader)
                    .orElseThrow(() -> new CustomException("Role is not found.", HttpStatus.NOT_FOUND));

            roles.add(assignRole);

            User user = User.builder()
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .email(registerRequest.getEmail())
                    .username(registerRequest.getUsername())
                    .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                    .isVerify(true)
                    .isEnabled(true)
                    .roles(roles)
                    .build();



            return userRepository.save(user);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update User Profile
     * */
    public User updateProfile(ProfileUpdate user, String username) throws CustomException{
        try{
            Optional<User> exitsUser = userRepository.findByUsername(username);

            if(exitsUser.isEmpty()){
                throw new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            exitsUser.get().setFirstName(user.getFirstName());
            exitsUser.get().setLastName(user.getLastName());
            exitsUser.get().setEmail(user.getEmail());
            exitsUser.get().setAffiliation(user.getAffiliation());
            exitsUser.get().setBiography(user.getBiography());
            exitsUser.get().setOrcid(user.getOrcid());
            exitsUser.get().setScopusId(user.getScopusId());
            exitsUser.get().setGoogleScholar(user.getGoogleScholar());
            exitsUser.get().setUsername(user.getUsername());
            exitsUser.get().setPhotoProfile(user.getPhotoProfile());
            exitsUser.get().setInterests(user.getInterests());

            return userRepository.save(exitsUser.get());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update Profile User
     * */
    public User updatePassword(PasswordRequest passwordRequest, String username) throws CustomException{
        try{
            Optional<User> exitsUser = userRepository.findByUsername(username);

            if(exitsUser.isEmpty()){
                throw new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Check if the current password matches the stored password
            if (!bCryptPasswordEncoder.matches(passwordRequest.getCurrentPassword(), exitsUser.get().getPassword())) {
                throw new CustomException("Current password is incorrect", HttpStatus.BAD_REQUEST);
            }

            // Check if the current password matches the stored new password
            if (passwordRequest.getPassword().equals(passwordRequest.getCurrentPassword())) {
                throw new CustomException("New password not available", HttpStatus.BAD_REQUEST);
            }

            // Update the password
            exitsUser.get().setPassword(bCryptPasswordEncoder.encode(passwordRequest.getPassword()));

            return userRepository.save(exitsUser.get());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete User Profile
     * */
    public User deleteAccount(String username) throws CustomException{
        try{
            Optional<User> exitsUser = userRepository.findByUsername(username);

            if(exitsUser.isEmpty()){
                throw new CustomException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            exitsUser.get().setEnabled(false);

            return userRepository.save(exitsUser.get());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    /**
     * Admin create User Profile
     * */
    public User createUser(User user) throws CustomException{
        try{
            if(userRepository.existsByEmail(user.getEmail()) || userRepository.existsByUsername(user.getUsername())){
                throw new CustomException(String.format("User with Email '%s' or Username '%s' not Available", user.getEmail(), user.getUsername()), HttpStatus.BAD_GATEWAY);
            }

            User userData = User.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .password(bCryptPasswordEncoder.encode(user.getPassword()))
                    .affiliation(user.getAffiliation())
                    .biography(user.getBiography())
                    .orcid(user.getOrcid())
                    .scopusId(user.getScopusId())
                    .googleScholar(user.getGoogleScholar())
                    .photoProfile(user.getPhotoProfile())
                    .interests(user.getInterests())
                    .isVerify(true)
                    .isEnabled(true)
                    .roles(user.getRoles())
                    .build();



            return userRepository.save(userData);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Get User By id
     * */
    public User getUser(String id) throws CustomException{
        try {
            Optional<User> user = userRepository.findById(id);

            if(user.isEmpty()){
                throw new CustomException("User with id " + id + " not found", HttpStatus.BAD_GATEWAY);
            }

            return user.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Get All User
     * */
    public Iterable<User> getAllUser() throws CustomException{
        try {
            return userRepository.findAll();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Disable User
     * */
    public void disableAccount(String id) throws CustomException{
        try {
            User user = this.getUser(id);

            user.setEnabled(false);

            userRepository.save(user);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Delete Permanent
     * */
    public void deleteUser(String id)  throws CustomException{
        try {
            this.getUser(id);

            userRepository.deleteById(id);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Multiple Delete Permanent
     * */
    public void multipleDelete(List<String> id)  throws CustomException{
        try {
            Iterable<User> users= userRepository.findAllById(id);

            userRepository.deleteAll(users);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Assign User Role
     * */
    public User assignRole(String id, List<Role> roles) throws CustomException{
        try {
            User user = this.getUser(id);

            Set<Role> setRoles = new HashSet<>(roles);
            user.getRoles().addAll(setRoles);

            return userRepository.save(user);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * User Assign Interest
     * */
    public User assignInterest(String username, List<String> interest) throws CustomException{
        try {
            Optional<User> user = userRepository.findByUsername(username);

            if(user.isEmpty()){
                throw new CustomException("User with username " + username + " not found", HttpStatus.BAD_GATEWAY);
            }

            user.get().setInterests(interest);

            return userRepository.save(user.get());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * User Update User
     * */
    public User updateUser(String id, User user) throws CustomException{
        try{
            User exitsUser = this.getUser(id);

            exitsUser.setFirstName(user.getFirstName());
            exitsUser.setLastName(user.getLastName());
            exitsUser.setEmail(user.getEmail());
            exitsUser.setAffiliation(user.getAffiliation());
            exitsUser.setBiography(user.getBiography());
            exitsUser.setOrcid(user.getOrcid());
            exitsUser.setScopusId(user.getScopusId());
            exitsUser.setGoogleScholar(user.getGoogleScholar());
            exitsUser.setUsername(user.getUsername());
            exitsUser.setPhotoProfile(user.getPhotoProfile());
            exitsUser.setInterests(user.getInterests());

            return userRepository.save(exitsUser);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public User getProfile(String username) throws CustomException{
        try{
            Optional<User> user = userRepository.findByUsername(username);

            if(user.isEmpty()){
                throw new CustomException("User with username " + username + " not found", HttpStatus.BAD_GATEWAY);
            }

            return user.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

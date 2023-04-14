package com.ipmugo.userservice.service;

import com.ipmugo.userservice.dto.*;
import com.ipmugo.userservice.model.*;
import com.ipmugo.userservice.repository.AuthorScholarMetricRepository;
import com.ipmugo.userservice.repository.AuthorScholarProfileRepository;
import com.ipmugo.userservice.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ipmugo.userservice.repository.RoleRepository;
import com.ipmugo.userservice.repository.UserRepository;
import com.ipmugo.userservice.utils.CustomException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private AuthorScholarMetricRepository authorScholarMetricRepository;

    @Autowired
    private AuthorScholarProfileRepository authorScholarProfileRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private WebClient.Builder webClientBuilder;

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
    public User registerUser(RegisterRequest registerRequest) throws CustomException {
        try {
            Optional<Role> role = roleRepository.findByName(UserRole.Reader);

            if (userRepository.existsByEmail(registerRequest.getEmail()) || userRepository.existsByUsername(registerRequest.getUsername()) || role.isEmpty()) {
                throw new CustomException(String.format("User with Email '%s' or Username '%s' not Available", registerRequest.getEmail(), registerRequest.getUsername()), HttpStatus.BAD_GATEWAY);
            }

            Role assignRole = roleRepository.findByName(UserRole.Reader)
                    .orElseThrow(() -> new CustomException("Role is not found.", HttpStatus.NOT_FOUND));

            User user = User.builder()
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .email(registerRequest.getEmail())
                    .username(registerRequest.getUsername())
                    .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                    .isVerify(true)
                    .isEnabled(true)
                    .build();


            User save = userRepository.save(user);

            assignRole.getUsers().add(save);

            roleRepository.save(assignRole);

            return save;
        } catch (Exception e) {
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
                    .isVerify(true)
                    .isEnabled(true)
                    .build();


            User save = userRepository.save(userData);

            for(Role role: user.getRoles()){
                Role assignRole = roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new CustomException("Role is not found.", HttpStatus.NOT_FOUND));

                assignRole.getUsers().add(save);

                roleRepository.save(assignRole);
            }

            return save;
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Get User By id
     * */
    public User getUser(UUID id) throws CustomException{
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
    public Page<User> getAllUser(Pageable pageable, String searchTerm) throws CustomException{
        try {
            if(searchTerm == null ||searchTerm.isEmpty()){
                return userRepository.findAll(pageable);
            }

            return userRepository.searchTerm(pageable, searchTerm);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Disable User
     * */
    public void disableAccount(UUID id) throws CustomException{
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
    public void deleteUser(UUID id)  throws CustomException{
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
    public void multipleDelete(List<UUID> id)  throws CustomException{
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
    public User assignRole(UUID id, List<Role> roles) throws CustomException{
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
     * User Update User
     * */
    public User updateUser(UUID id, User user) throws CustomException{
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

            return userRepository.save(exitsUser);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get User Profile
     * */
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

    /**
     * Forgot Password
     * */
    public  void forgotPassword(String email) throws CustomException{
        try{
            Optional<User> user = userRepository
                    .findByEmail(email);

            if(user.isEmpty()){
                throw new CustomException("User with email " + email + " not found", HttpStatus.BAD_GATEWAY);
            }

            /**
             * Password generator*/
            String password = bCryptPasswordEncoder.encode(passwordGenerator.generatePassword());


            user.get().setPassword(password);

            userRepository.save(user.get());

            String[] to = {user.get().getFirstName() + " " + user.get().getLastName() + "<" + email + ">"};

            MailRequest mailRequest = MailRequest.builder()
                    .from("Dian Dwi Vaputra" + "<dhyanputra24@gmail.com>")
                    .to(to)
                    .subject("New generate Password")
                    .body("<p>"+password+"</p>")
                    .build();

            webClientBuilder.build().post()
                    .uri("http://mail-service/api/send/mail")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mailRequest)
                    .retrieve().
                    bodyToMono(String.class).
                    block();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    /**
     * Async Google Scholar
     * */
    public  void asyncScholar(UUID id) throws CustomException{
        try{
            User user = this.getUser(id);

            if(user.getGoogleScholar() == null){
                throw new CustomException("User with id "+ id + " not found", HttpStatus.BAD_GATEWAY);
            }

            Connection.Response response = Jsoup.connect(
                            user.getGoogleScholar())
                    .timeout(0)
                    .execute();

            Document document = Jsoup.connect(
                            user.getGoogleScholar())
                    .timeout(0)
                    .get();

           if(response.statusCode() != 200){
               throw new CustomException("Connection loss", HttpStatus.BAD_GATEWAY);
           }

           List<Element> citation = document.getElementsByClass("gsc_rsb_std");

            Element profile = document.getElementById("gsc_prf_pu");

            if (profile != null) {
                profile = profile.select("img[src~=(?i)]").first();
            }

            if (profile != null) {
                user.setPhotoProfile(profile.attr("src"));
                userRepository.save(user);
            }

            ScholarProfile scholarProfile = ScholarProfile.builder()
                    .user(user)
                    .citations(Integer.valueOf(citation.get(0).text()))
                    .hIndex(Integer.valueOf(citation.get(2).text()))
                    .i10Index(Integer.valueOf(citation.get(4).text()))
                    .build();

            authorScholarProfileRepository.save(scholarProfile);

            Element statistic = document.getElementsByClass("gsc_md_hist_b").first();

            if (statistic != null) {
                Elements years = statistic.getElementsByClass("gsc_g_a");
                Elements counts = statistic.getElementsByClass("gsc_g_t");

                for (int x = 0; x < years.size(); x++) {
                    Element year = years.get(x);
                    Element count = counts.get(x);

                    ScholarMetric scholarMetric = ScholarMetric.builder()
                            .user(user)
                            .year(year.text())
                            .counts(Integer.valueOf(count.text()))
                            .build();

                    authorScholarMetricRepository.save(scholarMetric);
                }
            }


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

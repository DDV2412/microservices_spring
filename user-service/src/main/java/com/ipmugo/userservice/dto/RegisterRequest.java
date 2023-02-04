package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "FirstName cannot be blank")
    private String firstName;

    @NotBlank(message = "LastName cannot be blank")
    private  String lastName;

    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Password must be at least 10 characters long")
    private String password;

    private String affiliation;

    private String biography;

    private String orcid;

    private String scopusId;

    private String googleScholar;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 10, message = "Username must be at least 10 characters long")
    private String username;

    private boolean isVerify;

    private String photoProfile;

    private List<String> interests;

    private Set<String> roles;
}

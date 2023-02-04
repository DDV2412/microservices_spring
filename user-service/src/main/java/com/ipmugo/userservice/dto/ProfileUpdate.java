package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdate {

    @NotBlank(message = "FirstName cannot be blank")
    private String firstName;

    @NotBlank(message = "LastName cannot be blank")
    private  String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

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

}

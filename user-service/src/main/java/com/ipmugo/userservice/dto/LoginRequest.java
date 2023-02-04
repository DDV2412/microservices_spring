package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Password must be at least 10 characters long")
    private String password;
}

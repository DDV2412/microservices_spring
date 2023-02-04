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
public class PasswordRequest {

    @NotBlank(message = "Current password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Current password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Current password must be at least 10 characters long")
    private String currentPassword;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Password must be at least 10 characters long")
    private String password;

    @NotBlank(message = "Confirm password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Confirm password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Confirm password must be at least 10 characters long")
    private String confirmPassword;
}

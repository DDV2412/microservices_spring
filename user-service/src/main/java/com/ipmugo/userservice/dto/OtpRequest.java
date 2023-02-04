package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {

    @NotNull(message = "OTP cannot be null")
    @Min(value = 100000, message = "OTP must have at least 6 digits")
    private Integer otp;
}

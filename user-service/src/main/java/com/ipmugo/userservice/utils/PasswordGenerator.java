package com.ipmugo.userservice.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&";
    private static final String ALL_CHARS = UPPERCASE_CHARS + LOWERCASE_CHARS + NUMBERS + SPECIAL_CHARS;

    private static SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        while (password.length() < 10) {
            char randomChar = ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length()));
            if (UPPERCASE_CHARS.contains(String.valueOf(randomChar)) && !password.toString().matches("(?=.*[A-Z]).*")) {
                password.append(randomChar);
            } else if (LOWERCASE_CHARS.contains(String.valueOf(randomChar)) && !password.toString().matches("(?=.*[a-z]).*")) {
                password.append(randomChar);
            } else if (NUMBERS.contains(String.valueOf(randomChar)) && !password.toString().matches("(?=.*[0-9]).*")) {
                password.append(randomChar);
            } else if (SPECIAL_CHARS.contains(String.valueOf(randomChar)) && !password.toString().matches("(?=.*[!@#$%^&]).*")) {
                password.append(randomChar);
            }
        }
        return password.toString();
    }
}

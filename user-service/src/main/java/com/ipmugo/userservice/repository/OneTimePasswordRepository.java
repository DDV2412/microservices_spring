package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, UUID> {

    Optional<OneTimePassword> findByOtp(Integer otp);

    List<OneTimePassword> findAllByEmail(String email);
}

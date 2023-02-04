package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.OneTimePassword;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OneTimePasswordRepository extends MongoRepository<OneTimePassword, String> {

    Optional<OneTimePassword> findByOtp(Integer otp);

    List<OneTimePassword> findAllByEmail(String email);
}

package com.ipmugo.userservice.service;

import com.ipmugo.userservice.dto.VerifyRequest;
import com.ipmugo.userservice.repository.OneTimePasswordRepository;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ipmugo.userservice.model.OneTimePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneTimePasswordService {

    @Autowired
    private OneTimePasswordRepository oneTimePasswordRepository;

    /**
     * Generate OTP and Send OTP With Email Address
     * */
    public OneTimePassword generateOtp(VerifyRequest verifyRequest) throws CustomException {
        try{
            SecureRandom random = new SecureRandom();
            int num = 100000 + random.nextInt(900000);
            long expiredAt = 900;

            OneTimePassword otp = OneTimePassword.builder()
                    .otp(num)
                    .email(verifyRequest.getEmail())
                    .expiredAt(LocalDateTime.now().plusSeconds(expiredAt))
                    .build();

            return oneTimePasswordRepository.save(otp);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Verify OTP
     * */
    public OneTimePassword verifyOtp(Integer otp) throws CustomException{
        try{
            Optional<OneTimePassword> verifyOtp = oneTimePasswordRepository.findByOtp(otp);

            if(verifyOtp.isEmpty()){
                throw new CustomException("OTP is expired", HttpStatus.NOT_FOUND);
            }

            if(LocalDateTime.now().isAfter(verifyOtp.get().getExpiredAt())){
                throw new CustomException("OTP is expired", HttpStatus.BAD_GATEWAY);
            }

            return verifyOtp.get();

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete OTP After Register
     * */
    public void deleteOtp(String email) throws CustomException{
        try{
            List<OneTimePassword> listOtp = oneTimePasswordRepository.findAllByEmail(email);

            oneTimePasswordRepository.deleteAll(listOtp);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

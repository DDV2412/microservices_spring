package com.ipmugo.subscribeservice.service;

import com.ipmugo.subscribeservice.model.Subscribe;
import com.ipmugo.subscribeservice.repository.SubscribeRepository;
import com.ipmugo.subscribeservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeService {


    @Autowired
    private SubscribeRepository subscribeRepository;

    public Subscribe subscribe(Subscribe subscribe) throws CustomException {
        try{
           return subscribeRepository.save(subscribe);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public Page<Subscribe> getAllSubscribe(Pageable pageable) throws CustomException {
        try{
            return subscribeRepository.findAll(pageable);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void deleteSubscribe(UUID id) throws CustomException {
        try{
            subscribeRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

package com.ipmugo.searchservice.service;


import com.ipmugo.searchservice.model.Keyword;
import com.ipmugo.searchservice.repository.KeywordRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordService {

    @Autowired
    private KeywordRepository keywordRepository;

    public Keyword saveKeyword(Keyword keyword) throws CustomException{
        try{
            return keywordRepository.save(keyword);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

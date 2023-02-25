package com.ipmugo.searchservice.service;


import com.ipmugo.searchservice.model.Author;
import com.ipmugo.searchservice.repository.AuthorRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author saveAuthor(Author author) throws CustomException{
        try{
            return authorRepository.save(author);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

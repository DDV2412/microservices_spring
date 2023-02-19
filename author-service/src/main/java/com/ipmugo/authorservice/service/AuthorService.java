package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.model.Author;
import com.ipmugo.authorservice.repository.ArticleRepository;
import com.ipmugo.authorservice.repository.AuthorRepository;
import com.ipmugo.authorservice.repository.UserRepository;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorService {


    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Update Author
     * */
    public void updateAuthor(Author author) throws CustomException{
        try{
            authorRepository.save(author);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}

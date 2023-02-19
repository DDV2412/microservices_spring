package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.model.Article;
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
public class ArticleService {


    @Autowired
    private ArticleRepository articleRepository;

    public void updateArticle(Article Article) throws CustomException {
        try{
            articleRepository.save(Article);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

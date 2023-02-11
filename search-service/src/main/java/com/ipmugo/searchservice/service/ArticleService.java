package com.ipmugo.searchservice.service;

import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.repository.ArticleRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public void syncArticleDatabase(Article article){
        articleRepository.save(article);
    }

}

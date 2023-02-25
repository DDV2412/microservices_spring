package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.event.SetConterEvent;
import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.Status;
import com.ipmugo.authorservice.repository.ArticleRepository;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Article setCounterUpdate(SetConterEvent setConterEvent) throws CustomException {
        try{
            Optional<Article> article = articleRepository.findById(setConterEvent.getArticleId());

            if(article.isEmpty()){
                throw new CustomException("Article with id "+ setConterEvent.getArticleId()+ " not found", HttpStatus.NOT_FOUND);
            }

            if(setConterEvent.getStatus().equals(Status.Download)){
                article.get().setDownloadCount(setConterEvent.getCount());
            }

            if(setConterEvent.getStatus().equals(Status.View)){
                article.get().setDownloadCount(setConterEvent.getCount());
            }


            return articleRepository.save(article.get());


        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

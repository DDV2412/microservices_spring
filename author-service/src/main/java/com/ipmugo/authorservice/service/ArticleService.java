package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.model.Publication;
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

    public void updateArticle(Publication Article) throws CustomException {
        try{
            articleRepository.save(Article);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void updateCounter(Publication article) throws CustomException{
        try{
            Optional<Publication> article1 = articleRepository.findById(article.getId());

            if(article1.isPresent()){
                Publication update = Publication.builder()
                        .id(article.getId())
                        .citationByScopus(article.getCitationByScopus())
                        .citationByCrossRef(article.getCitationByCrossRef())
                        .viewsCount(article.getViewsCount())
                        .downloadCount(article.getDownloadCount())
                        .build();

                articleRepository.save(update);
            }


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}

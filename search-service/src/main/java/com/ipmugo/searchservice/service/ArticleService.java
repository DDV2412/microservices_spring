package com.ipmugo.searchservice.service;

import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.repository.ArticleRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;



    /**
     * @param article
     * @return
     * @throws CustomException
     */
    public Article createArticle(Article article) throws CustomException{
        try{
            return articleRepository.save(article);
        }catch (Exception e){

            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public Article updateCitationScopus(String id, int citation) throws CustomException{
        try{
            Optional<Article> articleOptional = articleRepository.findById(id);

            if(articleOptional.isEmpty()){
                throw new CustomException("Article with id "+ id + " not found", HttpStatus.NOT_FOUND);
            }

            articleOptional.get().setCitationByScopus(citation);

            return articleRepository.save(articleOptional.get());

        }catch (Exception e){

            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public Article updateCitationCrossRef(String id, int citation) throws CustomException{
        try{
            Optional<Article> articleOptional = articleRepository.findById(id);

            if(articleOptional.isEmpty()){
                throw new CustomException("Article with id "+ id + " not found", HttpStatus.NOT_FOUND);
            }

            articleOptional.get().setCitationByCrossRef(citation);

            return articleRepository.save(articleOptional.get());

        }catch (Exception e){

            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}

package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.Author;
import com.ipmugo.authorservice.model.User;
import com.ipmugo.authorservice.repository.ArticleRepository;
import com.ipmugo.authorservice.repository.AuthorRepository;
import com.ipmugo.authorservice.repository.UserRepository;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Assign Article with Author
     * */
    public void assignArticle (User user, List<Article> articles) throws CustomException{
        try{
            Optional<User> userOptional = userRepository.findById(user.getId());

            if(userOptional.isEmpty()){
                User save = userRepository.save(user);

                HashSet<Article> articleHashSet = new HashSet<>();
                for(Article article: articles){
                    Optional<Article> articleOptional = articleRepository.findById(article.getId());

                    if(articleOptional.isEmpty()){

                        HashSet<Author> authors = new HashSet<>();

                        for(Author author: article.getAuthors()){
                            Optional<Author> authorOptional = authorRepository.findById(author.getId());

                            if(authorOptional.isEmpty()){
                                Author authorBuild = Author.builder()
                                        .id(author.getId())
                                        .firstName(author.getFirstName())
                                        .lastName(author.getLastName())
                                        .affiliation(author.getAffiliation())
                                        .build();


                                authors.add(authorRepository.save(authorBuild));
                            }else{
                                authors.add(author);
                            }


                        }
                        Article articleBuild = Article.builder()
                                .id(article.getId())
                                .journal(article.getJournal())
                                .title(article.getTitle())
                                .publishDate(article.getPublishDate())
                                .doi(article.getDoi())
                                .volume(article.getVolume())
                                .issue(article.getIssue())
                                .articlePdf(article.getArticlePdf())
                                .authors(authors)
                                .citationByCrossRef(article.getCitationByCrossRef())
                                .citationByScopus(article.getCitationByScopus())
                                .viewsCount(article.getViewsCount())
                                .downloadCount(article.getDownloadCount())
                                .build();


                        articleHashSet.add(articleRepository.save(article));
                    }else{
                        articleHashSet.add(article);
                    }


                }

                save.setArticles(articleHashSet);

                userRepository.save(save);
            }else{
                HashSet<Article> articleHashSet = new HashSet<>();
                for(Article article: articles){
                    Optional<Article> articleOptional = articleRepository.findById(article.getId());

                    if(articleOptional.isEmpty()){

                        HashSet<Author> authors = new HashSet<>();

                        for(Author author: article.getAuthors()){
                            Optional<Author> authorOptional = authorRepository.findById(author.getId());

                            if(authorOptional.isEmpty()){
                                Author authorBuild = Author.builder()
                                        .id(author.getId())
                                        .firstName(author.getFirstName())
                                        .lastName(author.getLastName())
                                        .affiliation(author.getAffiliation())
                                        .build();


                                authors.add(authorRepository.save(authorBuild));
                            }else{
                                authors.add(author);
                            }


                        }
                        Article articleBuild = Article.builder()
                                .id(article.getId())
                                .journal(article.getJournal())
                                .title(article.getTitle())
                                .publishDate(article.getPublishDate())
                                .doi(article.getDoi())
                                .volume(article.getVolume())
                                .issue(article.getIssue())
                                .articlePdf(article.getArticlePdf())
                                .authors(authors)
                                .citationByCrossRef(article.getCitationByCrossRef())
                                .citationByScopus(article.getCitationByScopus())
                                .viewsCount(article.getViewsCount())
                                .downloadCount(article.getDownloadCount())
                                .build();


                        articleHashSet.add(articleRepository.save(article));
                    }else{
                        articleHashSet.add(article);
                    }


                }

                userOptional.get().setArticles(articleHashSet);

                userRepository.save(userOptional.get());
            }


        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get All Publication Author
     * */
    public Iterable<Article> getPublications(String id) throws CustomException{
        try{
            return articleRepository.findByAuthorAssign_Id(id);
        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Featured Authors
     * */
    public List<User> featuredAuthor() throws CustomException{
        try{
            return userRepository.findTop4ByOrderByHIndexAndArticleValuesDescWithLimit();
        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}

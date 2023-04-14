package com.ipmugo.authorservice.service;

import com.ipmugo.authorservice.model.Publication;
import com.ipmugo.authorservice.model.User;
import com.ipmugo.authorservice.repository.ArticleRepository;
import com.ipmugo.authorservice.repository.UserRepository;
import com.ipmugo.authorservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Assign Publication with Author
     * */
    public User assignArticle(User data) throws CustomException {
        try {
            Optional<User> userOptional = userRepository.findById(data.getId());

            User user;
            user = userOptional.orElseGet(() -> userRepository.save(User.builder()
                            .id(data.getId())
                            .firstName(data.getFirstName())
                            .lastName(data.getLastName())
                            .affiliation(data.getAffiliation())
                            .orcid(data.getOrcid())
                            .googleScholar(data.getGoogleScholar())
                            .citation(data.getCitation())
                            .profile(data.getProfile())
                            .publications(new HashSet<>())
                    .build()));

            Set<Publication> articleList = new HashSet<>();
            for (Publication article : data.getPublications()) {
                Optional<Publication> articleOptional = articleRepository.findById(article.getId());

                Publication savedArticle;
                if (articleOptional.isEmpty()) {
                    Publication articleBuilder = Publication.builder()
                            .id(article.getId())
                            .journal(article.getJournal())
                            .title(article.getTitle())
                            .publishDate(article.getPublishDate())
                            .doi(article.getDoi())
                            .volume(article.getVolume())
                            .issue(article.getIssue())
                            .articlePdf(article.getArticlePdf())
                            .citationByScopus(article.getCitationByScopus())
                            .citationByCrossRef(article.getCitationByCrossRef())
                            .viewsCount(article.getViewsCount())
                            .authors(new HashSet<>())
                            .downloadCount(article.getDownloadCount())
                            .build();
                    savedArticle = articleRepository.save(articleBuilder);
                } else {
                    savedArticle = articleOptional.get();
                }

                articleList.add(savedArticle);
            }

            for (Publication article : articleList) {
                article.getAuthors().add(user);
            }

            user.getPublications().addAll(articleList);
            return userRepository.save(user);

        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    /**
     * Get All Publication Author
     * */
    public Iterable<Publication> getPublications(String id) throws CustomException{
        try{
            Optional<User> user = userRepository.findById(id);

            if(user.isEmpty()){
                throw new CustomException("Author with ID "+ id + " not found", HttpStatus.NOT_FOUND);
            }

            return user.get().getPublications();

        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Featured Authors
     * */
    public List<User> featuredAuthor() throws CustomException{
        try{
            List<Object[]> authors =  userRepository.findUserByHighestCitationAndMostPublications();

            List<User> users = new ArrayList<>();
            for (Object[] row : authors) {
                User user = (User) row[0];
                users.add(user);
            }
            return users;
        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public Page<User> getAllAuthor(Pageable pageable) throws CustomException{
        try{
            return userRepository.findAll(pageable);
        }catch(Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Admin Get User By id
     * */
    public User getUser(String id) throws CustomException{
        try {
            Optional<User> user = userRepository.findById(id);

            if(user.isEmpty()){
                throw new CustomException("User with id " + id + " not found", HttpStatus.BAD_GATEWAY);
            }

            return user.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Async Google Scholar
     * */
    public  void asyncScholar(String id) throws CustomException{
        try{
            User user = this.getUser(id);

            if(user.getGoogleScholar() == null){
                throw new CustomException("User with id "+ id + " not found", HttpStatus.BAD_GATEWAY);
            }

            Connection.Response response = Jsoup.connect(
                            user.getGoogleScholar())
                    .timeout(0)
                    .execute();

            Document document = Jsoup.connect(
                            user.getGoogleScholar())
                    .timeout(0)
                    .get();

            if(response.statusCode() != 200){
                throw new CustomException("Connection loss", HttpStatus.BAD_GATEWAY);
            }

            List<Element> citation = document.getElementsByClass("gsc_rsb_std");

            Element profile = document.getElementById("gsc_prf_pu");


            user.setCitation(citation.get(2).text());

            userRepository.save(user);


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

}

package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    Page<Article> findAll(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.title LIKE %:searchTerm% OR a.doi LIKE %:searchTerm% OR a.abstractText LIKE %:searchTerm%")
    Page<Article> searchTerm(Pageable pageable, String searchTerm);

    Iterable<Article> findByJournalId(String journalId);

    Optional<Article> findByDoi(String doi);

    Optional<Article> findByTitleAndOjsId(String title, String ojsId);

    Iterable<Article> findByJournalIdOrderByIssueDescPublishDateDesc(String journalId);

    Iterable<Article> findTop4ByOrderByCitationByScopusDescCitationByCrossRefDesc();


}

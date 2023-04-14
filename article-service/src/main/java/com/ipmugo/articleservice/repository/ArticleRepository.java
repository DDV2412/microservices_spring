package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    Page<Article> findAll(Pageable pageable);

    @Query("{$text: {$search: ?0}}")
    Page<Article> searchTerm(String searchTerm, Pageable pageable);

    Iterable<Article> findByJournalId(String journalId);

    Optional<Article> findByDoi(String doi);

    Optional<Article> findByTitleAndOjsId(String title, String ojsId);

    Iterable<Article> findByJournalIdOrderByIssueDescPublishDateDesc(String journalId);

    Iterable<Article> findTop3ByOrderByCitationByScopusDescCitationByCrossRefDesc();


}

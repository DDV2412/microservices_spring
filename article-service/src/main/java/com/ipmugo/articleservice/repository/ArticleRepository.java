package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Article;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    Iterable<Article> findByJournalId(String journalId);

    Optional<Article> findByDoi(String doi);

    Optional<Article> findByTitleAndOjsId(String title, String ojsId);

    Iterable<Article> findByJournalIdOrderByIssueDescPublishDateDesc(String journalId);

}

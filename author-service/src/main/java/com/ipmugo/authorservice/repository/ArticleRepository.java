package com.ipmugo.authorservice.repository;

import com.ipmugo.authorservice.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, String> {

    Iterable<Article> findByAuthorAssign_Id(String userId);
}

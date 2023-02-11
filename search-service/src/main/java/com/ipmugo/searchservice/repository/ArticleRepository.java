package com.ipmugo.searchservice.repository;

import com.ipmugo.searchservice.model.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {
}

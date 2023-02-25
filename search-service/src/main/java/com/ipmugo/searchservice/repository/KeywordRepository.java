package com.ipmugo.searchservice.repository;


import com.ipmugo.searchservice.model.Keyword;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends ElasticsearchRepository<Keyword, String> {
}

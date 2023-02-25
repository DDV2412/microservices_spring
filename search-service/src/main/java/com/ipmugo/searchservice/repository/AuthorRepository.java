package com.ipmugo.searchservice.repository;


import com.ipmugo.searchservice.model.Author;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends ElasticsearchRepository<Author, String> {
}

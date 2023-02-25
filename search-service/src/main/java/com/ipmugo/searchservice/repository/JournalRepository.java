package com.ipmugo.searchservice.repository;


import com.ipmugo.searchservice.model.Journal;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends ElasticsearchRepository<Journal, String> {
}

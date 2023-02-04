package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Journal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalRepository extends MongoRepository<Journal, String> {
}

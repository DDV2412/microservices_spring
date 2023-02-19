package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface JournalRepository extends MongoRepository<Journal, UUID> {

    Page<Journal> findAll(Pageable pageable);
}

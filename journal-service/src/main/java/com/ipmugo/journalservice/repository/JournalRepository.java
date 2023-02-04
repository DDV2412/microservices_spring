package com.ipmugo.journalservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ipmugo.journalservice.model.Journal;

import java.util.Optional;

public interface JournalRepository extends MongoRepository<Journal, String> {
    Optional<Journal> findByIssn(String issn);
}

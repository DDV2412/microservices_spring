package com.ipmugo.mediaservice.repository;

import com.ipmugo.mediaservice.model.Document;
import com.ipmugo.mediaservice.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocumentRepository extends MongoRepository<Document, String> {
    Optional<Document> findByFileName(String name);
}

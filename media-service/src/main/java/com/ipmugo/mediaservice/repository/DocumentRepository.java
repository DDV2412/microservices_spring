package com.ipmugo.mediaservice.repository;

import com.ipmugo.mediaservice.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Page<Document> findAll(Pageable pageable);

    Optional<Document> findByFileName(String name);
}

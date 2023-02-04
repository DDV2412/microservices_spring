package com.ipmugo.mediaservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ipmugo.mediaservice.model.Media;

public interface MediaRepository extends MongoRepository<Media, String> {

    Optional<Media> findByFileName(String fileName);
}

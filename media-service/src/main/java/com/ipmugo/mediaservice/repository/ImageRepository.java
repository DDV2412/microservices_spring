package com.ipmugo.mediaservice.repository;

import com.ipmugo.mediaservice.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ImageRepository extends MongoRepository<Image, String> {

    Optional<Image> findByFileName(String name);
}

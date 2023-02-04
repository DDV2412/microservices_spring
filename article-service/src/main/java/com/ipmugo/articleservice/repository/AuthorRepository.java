package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthorRepository extends MongoRepository<Author, String> {

    Optional<Author> findAllByFirstNameAndLastName(String firstName, String lastName);

}

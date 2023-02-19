package com.ipmugo.articleservice.repository;

import com.ipmugo.articleservice.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AuthorRepository extends MongoRepository<Author, String> {

    @Query("SELECT a FROM Author a WHERE a.firstName LIKE %:firstName% AND a.doi LIKE %:lastName%")
    Page<Author> searchAuthor(Pageable pageable, String firstName, String lastName);

}

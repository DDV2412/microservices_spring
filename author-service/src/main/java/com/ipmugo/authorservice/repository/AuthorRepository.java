package com.ipmugo.authorservice.repository;

import com.ipmugo.authorservice.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, String> {
}

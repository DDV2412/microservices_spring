package com.ipmugo.authorservice.repository;

import com.ipmugo.authorservice.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Publication, String> {

}

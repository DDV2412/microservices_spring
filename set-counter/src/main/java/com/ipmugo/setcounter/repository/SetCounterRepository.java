package com.ipmugo.setcounter.repository;

import com.ipmugo.setcounter.model.SetCounter;
import com.ipmugo.setcounter.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SetCounterRepository extends MongoRepository<SetCounter, String> {

    Long countByArticleIdAndStatus(String articleId, Status status);
    Long countByStatus(Status status);

}

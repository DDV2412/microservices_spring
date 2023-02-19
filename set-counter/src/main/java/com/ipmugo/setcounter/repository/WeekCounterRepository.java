package com.ipmugo.setcounter.repository;

import com.ipmugo.setcounter.model.Status;
import com.ipmugo.setcounter.model.WeekCounter;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WeekCounterRepository extends MongoRepository<WeekCounter, String> {

    WeekCounter findFirstByWeekYearAndStatusOrderByCreatedAtDesc(String weekYear, Status status);

}

package com.ipmugo.setcounter.service;



import com.ipmugo.setcounter.event.SetCounterEvent;
import com.ipmugo.setcounter.model.SetCounter;
import com.ipmugo.setcounter.model.Status;
import com.ipmugo.setcounter.repository.SetCounterRepository;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetCounterService {

    @Autowired
    private SetCounterRepository setCounterRepository;

    @Autowired
    private KafkaTemplate<String, SetCounterEvent> kafkaTemplate;


    /**
     * Add View Counter by Article ID
     * */
    public void setCounter(String doi, Status status){
        SetCounter setCounter = SetCounter.builder()
                .doi(doi)
                .status(status).build();

        setCounterRepository.save(setCounter);
    }

    /**
     * Get Counter
     * */
    public long getCounter(Status status){
        return setCounterRepository.countByStatus(status);
    }

}

package com.ipmugo.setcounter.utils;

import com.ipmugo.setcounter.event.SetCounterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Async
@Component
public class Automate {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private KafkaTemplate<String, SetCounterEvent> kafkaTemplate;


    @Scheduled(cron = "* 1 * * * *", zone = "GMT+7")
    public void getSetCounter () {
        GroupOperation groupByDoi = group("doi")
                .first("doi").as("doi")
                .first("status").as("status")
                .count().as("count");

        Aggregation aggregation = newAggregation(
                sort(Sort.Direction.ASC, "doi"),
                groupByDoi
        );

        AggregationResults<SetCounterEvent> result = mongoOperations.aggregate(aggregation, "setCounter", SetCounterEvent.class);

        List<SetCounterEvent> results = result.getMappedResults();

        for(SetCounterEvent setCounterEvent: results){
            kafkaTemplate.send("setCounter", SetCounterEvent.builder()
                    .doi(setCounterEvent.getDoi())
                    .status(setCounterEvent.getStatus())
                    .count(setCounterEvent.getCount()).build());
        }
    }

}

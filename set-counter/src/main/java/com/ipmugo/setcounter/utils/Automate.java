package com.ipmugo.setcounter.utils;

import com.ipmugo.setcounter.event.SetConterEvent;
import com.ipmugo.setcounter.model.SetCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Async
@Component
public class Automate {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private KafkaTemplate<String, List<SetConterEvent>> kafkaTemplate;

    @Scheduled(cron = "0 1 * * * *", zone = "GMT+7")
    public void getSetCounter () {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("articleId", "status")
                        .count().as("count")
        );

        AggregationResults<SetConterEvent> results = mongoTemplate.aggregate(
                agg, SetCounter.class, SetConterEvent.class);

        List<SetConterEvent> groups = results.getMappedResults();

        kafkaTemplate.send("setConter", groups);

    }
}

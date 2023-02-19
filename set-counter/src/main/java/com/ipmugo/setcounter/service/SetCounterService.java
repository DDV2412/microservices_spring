package com.ipmugo.setcounter.service;



import com.ipmugo.setcounter.model.SetCounter;
import com.ipmugo.setcounter.model.Status;
import com.ipmugo.setcounter.model.WeekCounter;
import com.ipmugo.setcounter.repository.SetCounterRepository;
import com.ipmugo.setcounter.repository.WeekCounterRepository;
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
    private WeekCounterRepository weekCounterRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Add View Counter by Article ID
     * */
    public void setCounter(String articleId, Status status){
        SetCounter setCounter = SetCounter.builder()
                .articleId(articleId)
                .status(status).build();

        setCounterRepository.save(setCounter);
    }

    /**
     * Get Counter
     * */
    public long getCounter(Status status){
        return setCounterRepository.countByStatus(status);
    }

    /**
     * Get Counter
     * */
    public WeekCounter getPercentage(){
        LocalDateTime now = LocalDateTime.now();


        String weekYearLast = String.format("%04d-%02d", now.get(IsoFields.WEEK_BASED_YEAR), now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) - 1);
        WeekCounter lastWeek = mongoTemplate.findOne(Query.query(Criteria.where("weekYear").is(weekYearLast)), WeekCounter.class);

        String weekYearThis = String.format("%04d-%02d", now.get(IsoFields.WEEK_BASED_YEAR), now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));

        return mongoTemplate.findOne(Query.query(Criteria.where("weekYear").is(weekYearThis)), WeekCounter.class);
    }

    public void weekCounterData(Status status){
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("status").is(status)),
                Aggregation.project()
                        .and("createdAt").extractYear().as("year")
                        .and("createdAt").dateAsFormattedString("%Y-%W").as("weekYear")
                        .and("articleId").as("articleId"),
                Aggregation.group("year", "weekOfYear", "articleId").count().as("count"),
                Aggregation.group("year", "weekOfYear").push(new BasicDBObject("articleId", "$_id.articleId")
                        .append("count", "$count")).as("articleCounts"),
                Aggregation.sort(Sort.Direction.ASC, "year", "weekOfYear")
        );

        AggregationResults<WeekCounter> results = mongoTemplate.aggregate(agg, SetCounter.class, WeekCounter.class);
        List<WeekCounter> weekCounters = results.getMappedResults();


        for (WeekCounter weekCounter : weekCounters) {

            WeekCounter lastWeekCounter = weekCounterRepository.findFirstByWeekYearAndStatusOrderByCreatedAtDesc(
                    weekCounter.getWeekYear(), status);


            int increase = 0;
            if (lastWeekCounter != null) {
                increase = weekCounter.getCount() - lastWeekCounter.getCount();
            }


            weekCounter.setLastWeekCount(lastWeekCounter != null ? lastWeekCounter.getCount() : 0);
            weekCounter.setPercentageIncrease((double) increase / (double) weekCounter.getCount() * 100.0);
            weekCounter.setWeekYear(weekCounter.getWeekYear());
            weekCounter.setStatus(status);

            weekCounterRepository.save(weekCounter);
        }
    }
}

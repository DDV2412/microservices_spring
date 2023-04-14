package com.ipmugo.articleservice;

import com.ipmugo.articleservice.event.JournalEvent;
import com.ipmugo.articleservice.event.SetCounterEvent;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.service.ArticleService;
import com.ipmugo.articleservice.service.JournalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;


@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@EnableEurekaClient
public class ArticleServiceApplication {

        @Autowired
        private JournalService journalService;

        @Autowired
        private ArticleService articleService;

        private static final Logger logger = LoggerFactory.getLogger(ArticleServiceApplication.class);


        @KafkaListener(topics = "journal", groupId = "journalId", clientIdPrefix = "journalId")
        public void handleNotification(JournalEvent journalEvent) {
                Journal journal = Journal.builder()
                        .id(journalEvent.getId())
                        .name(journalEvent.getName())
                        .issn(journalEvent.getIssn())
                        .e_issn(journalEvent.getE_issn())
                        .publisher(journalEvent.getPublisher())
                        .abbreviation(journalEvent.getAbbreviation())
                        .journalSite(journalEvent.getJournalSite())
                        .scopusIndex(journalEvent.isScopusIndex())
                        .build();
                journalService.saveJournal(journal);
        }

        @KafkaListener(topics = "setCounter", groupId = "setCounterId", clientIdPrefix = "setCounterId")
        public void handleNotification(SetCounterEvent setCounterEvent) {
                if(!setCounterEvent.getDoi().isEmpty()){
                        articleService.setCounterUpdate(setCounterEvent);
                }
        }

        public static void main(String[] args) {
                SpringApplication.run(ArticleServiceApplication.class, args);
        }

}
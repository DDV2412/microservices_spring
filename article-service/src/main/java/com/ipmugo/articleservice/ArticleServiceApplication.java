package com.ipmugo.articleservice;

import com.ipmugo.articleservice.event.JournalEvent;
import com.ipmugo.articleservice.event.SetConterEvent;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.service.ArticleService;
import com.ipmugo.articleservice.service.JournalService;
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

        @KafkaListener(topics = "setConter", groupId = "setCounterId", clientIdPrefix = "setCounterId")
        public void handleNotification(List<SetConterEvent> setConterEvents) {
                for (SetConterEvent setConterEvent: setConterEvents){
                        articleService.setCounterUpdate(setConterEvent);
                }
        }

        public static void main(String[] args) {
                SpringApplication.run(ArticleServiceApplication.class, args);
        }

}
package com.ipmugo.articleservice;

import com.ipmugo.articleservice.event.JournalEvent;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.KafkaListener;


@SpringBootApplication
@EnableMongoAuditing
@EnableEurekaClient
public class ArticleServiceApplication {

        @Autowired
        private JournalService journalService;

        @KafkaListener(topics = "journal")
        public void handleNotification(JournalEvent journalEvent) {
                Journal journal = Journal.builder()
                        .id(journalEvent.getId())
                        .name(journalEvent.getName())
                        .issn(journalEvent.getIssn())
                        .e_issn(journalEvent.getE_issn())
                        .publisher(journalEvent.getPublisher())
                        .abbreviation(journalEvent.getAbbreviation())
                        .journalSite(journalEvent.getJournalSite())
                        .build();
                journalService.saveJournal(journal);
        }

        public static void main(String[] args) {
                SpringApplication.run(ArticleServiceApplication.class, args);
        }

}
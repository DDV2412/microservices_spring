package com.ipmugo.authorservice;

import com.ipmugo.authorservice.event.SetConterEvent;
import com.ipmugo.authorservice.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;


@SpringBootApplication
@EnableEurekaClient
public class AuthorServiceApplication {

    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = "setConter", groupId = "setCounterId", clientIdPrefix = "setCounterId")
    public void handleNotification(List<SetConterEvent> setConterEvents) {
        for (SetConterEvent setConterEvent: setConterEvents){
            articleService.setCounterUpdate(setConterEvent);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthorServiceApplication.class, args);
    }
}
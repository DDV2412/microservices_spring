package com.ipmugo.authorservice;


import com.ipmugo.authorservice.event.UpdateCounter;
import com.ipmugo.authorservice.model.Publication;
import com.ipmugo.authorservice.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;


@SpringBootApplication
@EnableEurekaClient
public class AuthorServiceApplication {


    @Autowired
    private ArticleService articleService;

    private static final Logger logger = LoggerFactory.getLogger(AuthorServiceApplication.class);

    @KafkaListener(topics = "setUpdate", groupId = "setUpdateId", clientIdPrefix = "setUpdateId")
    public void handleUpdate(UpdateCounter updateCounter) {

        try{
            /**
             * Build Article For Kafka template
             * */

            Publication article = Publication.builder()
                    .id(updateCounter.getArticleId())
                    .citationByScopus(updateCounter.getCitationByScopus())
                    .citationByCrossRef(updateCounter.getCitationByCrossRef())
                    .viewsCount(updateCounter.getViewsCount())
                    .downloadCount(updateCounter.getDownloadCount())
                    .build();

            articleService.updateCounter(article);
        } catch (Exception ex) {
            logger.error("Failed to save Article to Elasticsearch: " + ex.getMessage(), ex);
            throw ex;
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(AuthorServiceApplication.class, args);
    }
}
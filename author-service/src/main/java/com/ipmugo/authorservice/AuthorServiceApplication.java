package com.ipmugo.authorservice;

import com.ipmugo.authorservice.event.ArticleAssignEvent;
import com.ipmugo.authorservice.model.Article;
import com.ipmugo.authorservice.model.Author;
import com.ipmugo.authorservice.service.ArticleService;
import com.ipmugo.authorservice.service.AuthorService;
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

    @Autowired
    private AuthorService authorService;

    @KafkaListener(topics = "articleAssign", groupId = "articleAssignId")
    public void handleNotification(ArticleAssignEvent articleAssignEvent) {
        Article article = Article.builder()
                .id(articleAssignEvent.getId())
                .journal(articleAssignEvent.getJournal())
                .title(articleAssignEvent.getTitle())
                .publishDate(articleAssignEvent.getPublishDate())
                .volume(articleAssignEvent.getVolume())
                .issue(articleAssignEvent.getIssue())
                .doi(articleAssignEvent.getDoi())
                .articlePdf(articleAssignEvent.getArticlePdf())
                .citationByScopus(articleAssignEvent.getCitationByScopus())
                .citationByCrossRef(articleAssignEvent.getCitationByCrossRef())
                .viewsCount(articleAssignEvent.getViewsCount())
                .downloadCount(articleAssignEvent.getDownloadCount())
                .build();

        articleService.updateArticle(article);
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthorServiceApplication.class, args);
    }
}
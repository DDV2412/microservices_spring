package com.ipmugo.searchservice;

import com.ipmugo.searchservice.event.ArticleEvent;
import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableEurekaClient
@EnableElasticsearchRepositories(basePackages = "com.ipmugo.searchservice.repository")
public class SearchSearchApplication {

    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = "article")
    public void handleNotification(ArticleEvent articleEvent) {
        Article article = Article.builder()
                .id(articleEvent.getId())
                .journal(articleEvent.getJournal())
                .title(articleEvent.getTitle())
                .pages(articleEvent.getPages())
                .publishYear(articleEvent.getPublishYear())
                .lastModifier(articleEvent.getLastModifier())
                .publishDate(articleEvent.getPublishDate())
                .doi(articleEvent.getDoi())
                .volume(articleEvent.getVolume())
                .issue(articleEvent.getIssue())
                .publishStatus(articleEvent.getPublishStatus())
                .abstractText(articleEvent.getAbstractText())
                .keywords(articleEvent.getKeywords())
                .authors(articleEvent.getAuthors())
                .citationByScopus(articleEvent.getCitationByScopus())
                .citationByCrossRef(articleEvent.getCitationByCrossRef())
                .build();

        articleService.syncArticleDatabase(article);

    }

    public static void main(String[] args) {
        SpringApplication.run(SearchSearchApplication.class, args);
    }

}
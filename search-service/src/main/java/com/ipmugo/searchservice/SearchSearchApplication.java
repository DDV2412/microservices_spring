package com.ipmugo.searchservice;

import com.ipmugo.searchservice.event.ArticleEvent;
import com.ipmugo.searchservice.event.AuthorEvent;
import com.ipmugo.searchservice.event.KeywordEvent;
import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.model.Author;
import com.ipmugo.searchservice.model.Journal;
import com.ipmugo.searchservice.model.Keyword;
import com.ipmugo.searchservice.service.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.kafka.annotation.KafkaListener;



import java.util.HashSet;


@SpringBootApplication
@EnableEurekaClient
@EnableElasticsearchRepositories(basePackages = "com.ipmugo.searchservice.repository")
public class SearchSearchApplication {


    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = "articleScopusCitation")
    public void handleCitationScopus(ArticleEvent articleEvent) {
        articleService.updateCitationScopus(articleEvent.getId(), articleEvent.getCitationByScopus());
    }

    @KafkaListener(topics = "articleCrossRefCitation")
    public void handleCitationCrossRef(ArticleEvent articleEvent) {

        articleService.updateCitationCrossRef(articleEvent.getId(), articleEvent.getCitationByCrossRef());

    }

    @KafkaListener(topics = "article")
    public void handleNotification(ArticleEvent articleEvent) {
        Journal journal = Journal.builder()
                .id(articleEvent.getJournal().getId())
                .name(articleEvent.getJournal().getName())
                .issn(articleEvent.getJournal().getIssn())
                .e_issn(articleEvent.getJournal().getE_issn())
                .publisher(articleEvent.getJournal().getPublisher())
                .build();

        HashSet<Author> authors = new HashSet<>();

        for(AuthorEvent authorEvent: articleEvent.getAuthorEvents()){
            Author author = Author.builder()
                    .id(authorEvent.getId())
                    .firstName(authorEvent.getFirstName())
                    .lastName(authorEvent.getLastName())
                    .affiliation(authorEvent.getAffiliation())
                    .build();

            authors.add(author);
        }

        HashSet<Keyword> keywords = new HashSet<>();

        if(!articleEvent.getKeywordEvents().isEmpty()){
            for(KeywordEvent keyword : articleEvent.getKeywordEvents()){
                Keyword keyword1 = Keyword.builder()
                        .name(keyword.getName()).build();

                keywords.add(keyword1);
            }
        }

        Article article = Article.builder()
                .id(articleEvent.getId())
                .journal(journal)
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
                .keywords(keywords)
                .authors(authors)
                .citationByScopus(articleEvent.getCitationByScopus())
                .citationByCrossRef(articleEvent.getCitationByCrossRef())
                .downloadCount(articleEvent.getDownloadCount())
                .viewsCount(articleEvent.getViewsCount())
                .build();

        articleService.createArticle(article);

    }


    public static void main(String[] args) {
        SpringApplication.run(SearchSearchApplication.class, args);
    }

}
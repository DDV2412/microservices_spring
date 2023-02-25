package com.ipmugo.searchservice;


import com.ipmugo.searchservice.event.ArticleEvent;
import com.ipmugo.searchservice.event.AuthorEvent;
import com.ipmugo.searchservice.event.KeywordEvent;
import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.model.Author;
import com.ipmugo.searchservice.model.Journal;
import com.ipmugo.searchservice.model.Keyword;
import com.ipmugo.searchservice.service.ArticleService;
import com.ipmugo.searchservice.service.AuthorService;
import com.ipmugo.searchservice.service.JournalService;
import com.ipmugo.searchservice.service.KeywordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@SpringBootApplication
@EnableEurekaClient
public class SearchSearchApplication {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private JournalService journalService;

    @Autowired
    private KeywordService keywordService;

    private static final Logger logger = LoggerFactory.getLogger(SearchSearchApplication.class);


    @KafkaListener(topics = "article", groupId = "articleId", clientIdPrefix = "articleId")
    public void handleNotification(ArticleEvent articleEvent) {

        try{
            /**
             * Build Article For Kafka template
             * */

            Journal journal = Journal.builder()
                    .id(articleEvent.getJournal().getId().toString())
                    .name(articleEvent.getJournal().getName())
                    .issn(articleEvent.getJournal().getIssn())
                    .e_issn(articleEvent.getJournal().getE_issn())
                    .publisher(articleEvent.getJournal().getPublisher())
                    .abbreviation(articleEvent.getJournal().getAbbreviation())
                    .journalSite(articleEvent.getJournal().getJournalSite())
                    .scopusIndex(articleEvent.getJournal().isScopusIndex())
                    .build();

            List<Keyword> keywords = new ArrayList<>();

            if(articleEvent.getKeywords().size() > 0){
                for(KeywordEvent keyword: articleEvent.getKeywords()){
                    Keyword keywordEvent = Keyword.builder()
                            .name(keyword.getName()).build();

                    keywords.add(keywordService.saveKeyword(keywordEvent));
                }
            }

            HashSet<Author> authors = new HashSet<>();

            if(articleEvent.getAuthors().size() > 0){
                for(AuthorEvent author: articleEvent.getAuthors()){
                    Author authorEvent = Author.builder()
                            .id(author.getId())
                            .firstName(author.getFirstName())
                            .lastName(author.getLastName())
                            .email(author.getEmail())
                            .affiliation(author.getAffiliation())
                            .orcid(author.getOrcid())
                            .build();

                    authors.add(authorService.saveAuthor(authorEvent));
                }
            }

            Article article = Article.builder()
                    .id(articleEvent.getId())
                    .journal(journalService.saveJournal(journal))
                    .ojsId(articleEvent.getOjsId())
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
                    .articlePdf(articleEvent.getArticlePdf())
                    .keywords(keywords)
                    .authors(authors)
                    .citationByScopus(articleEvent.getCitationByScopus())
                    .citationByCrossRef(articleEvent.getCitationByCrossRef())
                    .figures(articleEvent.getFigures())
                    .viewsCount(articleEvent.getViewsCount())
                    .downloadCount(articleEvent.getDownloadCount())
                    .build();

            articleService.saveArticle(article);
        } catch (Exception ex) {
            logger.error("Failed to save Article to Elasticsearch: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SearchSearchApplication.class, args);
    }

}
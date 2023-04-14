package com.ipmugo.articleservice.utils;

import com.ipmugo.articleservice.model.Article;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.service.ArticleService;
import com.ipmugo.articleservice.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Async
public class Automate {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JournalService journalService;

    @Scheduled(cron = "0 0 0 18 * *", zone = "GMT+7")
    public void syncronizedCitationScopus () {
        Pageable pageable = PageRequest.of(0, 25);


        do{
            Page<Article> articles = articleService.getAllArticle(pageable, null);

            if(articles.getContent().size() == 0){
                break;
            }

            for(Article article: articles.getContent()){
                if(article.getDoi().isEmpty() && !article.getJournal().isScopusIndex()){
                    continue;
                }

                try{
                    articleService.citationScopus(article.getDoi());
                }catch (Exception e){
                    continue;
                }
            }
        }while (true);
    }

    @Scheduled(cron = "0 0 0 16 * *", zone = "GMT+7")
    public void syncronizedCitationCrossRef () {
        Pageable pageable = PageRequest.of(0, 25);


        do{
            Page<Article> articles = articleService.getAllArticle(pageable, "");

            if(articles.getContent().size() == 0){
                break;
            }

            for(Article article: articles.getContent()){
                if(article.getDoi().isEmpty()){
                    continue;
                }
                try{
                    articleService.citationCrossRef(article.getDoi());
                }catch (Exception e){
                    continue;
                }
            }
        }while (true);
    }

    @Scheduled(cron = "0 0 0 15 * *", zone = "GMT+7")
    public void continueHarvesting () {
        Pageable pageable = PageRequest.of(0, 25);

        Date until = new Date();
        LocalDate untilLocalDate = LocalDate.ofInstant(until.toInstant(), ZoneId.systemDefault());
        LocalDate startLocalDate = untilLocalDate.minus(Period.ofMonths(1));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = startLocalDate.format(formatter);
        String untilDate = untilLocalDate.format(formatter);

        do{
            Page<Journal> journals = journalService.getAllJournal(pageable);

            if(journals.getContent().size() == 0){
                break;
            }

            for(Journal journal: journals.getContent()){
                try{
                    articleService.getOaiPmh(journal.getAbbreviation(), "oai_dc", startDate, untilDate);
                }catch (Exception e){
                    continue;
                }
            }
        }while (true);
    }
}

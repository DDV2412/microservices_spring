package com.ipmugo.journalservice.utils;

import com.ipmugo.journalservice.model.Journal;
import com.ipmugo.journalservice.service.CitationReportService;
import com.ipmugo.journalservice.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
public class Automate {

    @Autowired
    private JournalService journalService;

    @Autowired
    private CitationReportService citationReportService;

    @Scheduled(cron = "0 0 14 21 * *", zone = "GMT+7")
    public void syncronizedMetric(){
        Pageable pageable = PageRequest.of(0, 25);

        do{
            Page<Journal> journals = journalService.getAllJournals(pageable, null);

            if(journals.getContent().size() == 0){
                break;
            }
            for (Journal journal: journals){
               try{
                   citationReportService.syncCitationReport(journal.getIssn());
               }catch (Exception e){
                   continue;
               }
            }
        }while (true);

    }
}

package com.ipmugo.journalservice.service;

import com.ipmugo.journalservice.dto.*;
import com.ipmugo.journalservice.event.JournalEvent;
import com.ipmugo.journalservice.model.CitationReport;
import com.ipmugo.journalservice.model.Journal;
import com.ipmugo.journalservice.repository.CitationReportRepository;
import com.ipmugo.journalservice.repository.JournalRepository;
import com.ipmugo.journalservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CitationReportService {

    @Autowired
    private CitationReportRepository citationReportRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    JournalRepository journalRepository;

    @Value("${scopus.api.key.secret}")
    private String apiKey;

    @Autowired
    private KafkaTemplate<String, JournalEvent> kafkaTemplate;

    /**
     * Sync Journal Citation Report
     * */
    public <T> CitationReport syncCitationReport(String issn)throws CustomException {
        try{

            Optional<Journal> journal = journalRepository.findByIssn(issn);

            if(journal.isEmpty()){
                throw new CustomException("Journal with issn " + issn +" not found", HttpStatus.NOT_FOUND);
            }

            JournalCitation journalCitation = webClientBuilder.build().get()
                    .uri("https://api.elsevier.com/content/serial/title?issn=" + issn).
                    header("X-ELS-APIKey", apiKey).
                    retrieve().
                    bodyToMono(JournalCitation.class).
                    block();

            if(journalCitation == null){
                throw new CustomException("Journal citation report not registered", HttpStatus.NO_CONTENT);
            }

            if(journalCitation.getSerialMetadataResponse().getError() != null){
                throw new CustomException(journalCitation.getSerialMetadataResponse().getError(), HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
            }

            if(journalCitation.getSerialMetadataResponse().getEntry().isEmpty()){
                throw new CustomException("Journal citation report is entry empty", HttpStatus.NO_CONTENT);
            }

            List<CitationReportScopus>  citationReportScopuses = journalCitation.getSerialMetadataResponse().getEntry();

            List<Snip> snip = citationReportScopuses.get(0).getSNIPList().getSnip();
            List<Sjr> sjr = citationReportScopuses.get(0).getSJRList().getSjr();
            CiteScoreYearInfoList citeScoreYearInfoLists = citationReportScopuses.get(0).getCiteScoreYearInfoList();

            CitationReport citationReport = CitationReport.builder()
                    .journal(journal.get())
                    .sjr(Double.parseDouble(sjr.get(0).getData()))
                    .snip(Double.parseDouble(snip.get(0).getData()))
                    .citeScoreCurrent(Double.parseDouble(citeScoreYearInfoLists.getCiteScoreCurrentMetric()))
                    .citeScoreTracker(Double.parseDouble(citeScoreYearInfoLists.getCiteScoreTracker()))
                    .currentYear(citeScoreYearInfoLists.getCiteScoreCurrentMetricYear())
                    .trackerYear(citeScoreYearInfoLists.getCiteScoreTrackerYear())
                    .build();

            journal.get().setJournalCitationReport(citationReportRepository.save(citationReport));

            Journal saveJournal = journalRepository.save(journal.get());

            kafkaTemplate.send("journal", JournalEvent.builder()
                    .id(saveJournal.getId())
                    .name(saveJournal.getName())
                    .issn(saveJournal.getIssn())
                    .e_issn(saveJournal.getE_issn())
                    .publisher(saveJournal.getPublisher())
                    .abbreviation(saveJournal.getAbbreviation())
                    .journalSite(saveJournal.getJournalSite())
                            .scopusIndex(true)
                    .build());

            return citationReport;

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

package com.ipmugo.journalservice.service;

import java.util.List;
import java.util.Optional;

import com.ipmugo.journalservice.event.JournalEvent;
import com.ipmugo.journalservice.model.Category;
import com.ipmugo.journalservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ipmugo.journalservice.dto.JournalRequest;
import com.ipmugo.journalservice.model.Journal;
import com.ipmugo.journalservice.repository.JournalRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {


    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private KafkaTemplate<String, JournalEvent> kafkaTemplate;

    /**
     * Save or Update Journal
     * */
    public Journal createOrUpdateJournal(JournalRequest journalRequest) throws CustomException {
        try{
            Optional<Journal> isExist = journalRepository.findByIssn(journalRequest.getIssn());

            if(isExist.isPresent()){
                Journal journal = isExist.get();
                journal.setName(journalRequest.getName());
                        journal.setIssn(journalRequest.getIssn());
                        journal.setE_issn(journalRequest.getE_issn());
                        journal.setAbbreviation(journalRequest.getAbbreviation());
                        journal.setThumbnail(journalRequest.getThumbnail());
                        journal.setDescription(journalRequest.getDescription());
                        journal.setPublisher(journalRequest.getPublisher());
                        journal.setJournalSite(journalRequest.getJournalSite());
                        journal.setCountry(journalRequest.getCountry());
                        journal.setFocusScope(journalRequest.getFocusScope());
                        journal.setAuthorGuidelines(journalRequest.getAuthorGuidelines());
                        journal.setPrivacyStatement(journalRequest.getPrivacyStatement());
                        journal.setAuthorFees(journalRequest.getAuthorFees());
                        journal.setReviewPolice(journalRequest.getReviewPolice());
                        journal.setLicense(journalRequest.getLicense());

                kafkaTemplate.send("journal", JournalEvent.builder()
                        .id(journal.getId())
                        .name(journal.getName())
                        .issn(journal.getIssn())
                        .e_issn(journal.getE_issn())
                        .publisher(journal.getPublisher())
                        .abbreviation(journal.getAbbreviation())
                        .journalSite(journal.getJournalSite())
                        .build());
                return journalRepository.save(journal);

            }

            Journal journal = Journal.builder()
                    .name(journalRequest.getName())
                    .issn(journalRequest.getIssn())
                    .e_issn(journalRequest.getE_issn())
                    .abbreviation(journalRequest.getAbbreviation())
                    .thumbnail(journalRequest.getThumbnail())
                    .description(journalRequest.getDescription())
                    .publisher(journalRequest.getPublisher())
                    .journalSite(journalRequest.getJournalSite())
                    .country(journalRequest.getCountry())
                    .focusScope(journalRequest.getFocusScope())
                    .authorGuidelines(journalRequest.getAuthorGuidelines())
                    .privacyStatement(journalRequest.getPrivacyStatement())
                    .authorFees(journalRequest.getAuthorFees())
                    .reviewPolice(journalRequest.getReviewPolice())
                    .license(journalRequest.getLicense())
                    .build();

            Journal saveJournal = journalRepository.save(journal);

            kafkaTemplate.send("journal", JournalEvent.builder()
                    .id(saveJournal.getId())
                    .name(saveJournal.getName())
                    .issn(saveJournal.getIssn())
                    .e_issn(saveJournal.getE_issn())
                    .publisher(saveJournal.getPublisher())
                    .abbreviation(saveJournal.getAbbreviation())
                    .journalSite(saveJournal.getJournalSite())
                    .build());

            return saveJournal;
        }catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get List Journal
     * */
    public List<Journal> getAllJournals() throws CustomException {
        try{
            return journalRepository.findAll();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Journal By ID
     * */
    public Journal getJournal(String id) throws CustomException {
        try{
            Optional<Journal> journal = journalRepository.findById(id);
            if (journal.isEmpty()) {
                throw new CustomException("Journal with " + id +" not found", HttpStatus.NOT_FOUND);
            }
            return journal.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete Journal By ID
     * */
    public void deleteJournal(String id) throws CustomException {
        try{

            this.getJournal(id);

            journalRepository.deleteById(id);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Assign All Categories to Journal By ID
     * */
    public Journal assignCategory(String id, List<Category> category) throws CustomException{
        try{
            Journal journal = this.getJournal(id);

            journal.getCategoriesList().addAll(category);

            return journalRepository.save(journal);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}
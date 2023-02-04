package com.ipmugo.articleservice.service;

import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.model.Journal;
import com.ipmugo.articleservice.repository.AuthorRepository;
import com.ipmugo.articleservice.repository.JournalRepository;
import com.ipmugo.articleservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    /**
     * Save Journal
     * */
    public Journal saveJournal(Journal journal) throws CustomException {
        try{
            return journalRepository.save(journal);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Journal By Id
     * */
    public Journal getJournal(String id) throws CustomException {
        try{
            Optional<Journal> journal = journalRepository.findById(id);

            return journal.orElse(null);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
    /**
     * Delete Journal
     * */
    public void deleteJournal(String id) throws CustomException {
        try{
            journalRepository.deleteById(id);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

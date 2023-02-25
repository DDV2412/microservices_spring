package com.ipmugo.searchservice.service;


import com.ipmugo.searchservice.model.Journal;
import com.ipmugo.searchservice.repository.JournalRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    public Journal saveJournal(Journal journal) throws CustomException{
        try{
            return journalRepository.save(journal);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}

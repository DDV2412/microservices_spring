package com.ipmugo.journalservice.repository;


import com.ipmugo.journalservice.model.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JournalRepository extends JpaRepository<Journal, UUID> {
    Optional<Journal> findByIssn(String issn);

    Optional<Journal> findByAbbreviation(String abbreviation);

    Page<Journal> findAll(Pageable pageable);

    @Query("SELECT j FROM Journal j WHERE j.name LIKE %:searchTerm% OR j.issn LIKE %:searchTerm%")
    Page<Journal> searchTerm(Pageable pageable, String searchTerm);
}

package com.ipmugo.journalservice.repository;

import com.ipmugo.journalservice.model.CitationReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CitationReportRepository extends JpaRepository<CitationReport, UUID> {
}

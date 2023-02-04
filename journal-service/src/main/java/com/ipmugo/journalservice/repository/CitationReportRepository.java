package com.ipmugo.journalservice.repository;

import com.ipmugo.journalservice.model.CitationReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CitationReportRepository extends MongoRepository<CitationReport, String> {
}

package com.ipmugo.journalservice.controller;

import com.ipmugo.journalservice.dto.ResponseData;
import com.ipmugo.journalservice.model.CitationReport;
import com.ipmugo.journalservice.service.CitationReportService;
import com.ipmugo.journalservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/citation-report")
@RequiredArgsConstructor
public class CitationReportController {

    @Autowired
    private CitationReportService citationReportService;


    /**
     * Sync Journal Citation By ISSN
     * */
    @GetMapping("/{issn}")
    public ResponseEntity<ResponseData<CitationReport>> syncCitationReport(@PathVariable("issn") String issn){
        ResponseData<CitationReport> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(citationReportService.syncCitationReport(issn));
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

}

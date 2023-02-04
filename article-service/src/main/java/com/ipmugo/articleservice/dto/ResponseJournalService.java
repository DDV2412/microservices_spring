package com.ipmugo.articleservice.dto;

import com.ipmugo.articleservice.model.Journal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseJournalService {
    private boolean status;

    private List<String> messages = new ArrayList<>();

    private Journal data;
}

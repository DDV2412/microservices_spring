package com.ipmugo.journalservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalEvent {

    private UUID id;

    private String name;

    private String issn;

    private String e_issn;

    private String abbreviation;

    private String publisher;

    private String journalSite;

    private boolean scopusIndex;
}

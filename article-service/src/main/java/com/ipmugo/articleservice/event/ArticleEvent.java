package com.ipmugo.articleservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleEvent {

    private String id;

    private Journal journal;

    private String title;

    private String pages;

    private String publishYear;

    private String lastModifier;

    private String publishDate;

    private String doi;

    private String volume;

    private String issue;

    private String publishStatus;

    private String abstractText;

    private List<HashMap<String, String>> keywords;

    private Set<AuthorEvent> authorEvents;

    private CitationScopusEvent citationByScopus;

    private CitationCrossRefEvent citationByCrossRef;

}






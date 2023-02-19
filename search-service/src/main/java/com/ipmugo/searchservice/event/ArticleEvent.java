package com.ipmugo.searchservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleEvent {

    private String id;

    private JournalEvent journal;

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

    private Set<KeywordEvent> keywordEvents;

    private Set<AuthorEvent> authorEvents;

    private List<String> figures;

    private int citationByScopus;

    private int citationByCrossRef;

    private int viewsCount;

    private  int downloadCount;

}






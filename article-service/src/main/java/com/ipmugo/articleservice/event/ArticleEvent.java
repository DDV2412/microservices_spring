package com.ipmugo.articleservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleEvent {


    private String id;


    private JournalEvent journal;


    private String ojsId;


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


    private String articlePdf;


    private List<KeywordEvent> keywords;


    private Set<AuthorEvent> authors;

    private int citationByScopus;

    private int citationByCrossRef;

    private List<String> figures;

    private long viewsCount;

    private long downloadCount;
}

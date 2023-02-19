package com.ipmugo.authorservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAssignEvent {

    private String id;


    private String journal;


    private String title;




    private String publishDate;


    private String doi;


    private String volume;


    private String issue;


    private String articlePdf;



    private int citationByScopus;

    private int citationByCrossRef;

    private int viewsCount;

    private  int downloadCount;
}

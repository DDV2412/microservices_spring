package com.ipmugo.articleservice.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCounter {
    private String articleId;
    private int citationByScopus;

    private int citationByCrossRef;

    private long viewsCount;

    private long downloadCount;
}

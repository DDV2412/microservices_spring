package com.ipmugo.searchservice.event;

import com.ipmugo.searchservice.model.Author;
import com.ipmugo.searchservice.model.CitationCrossRef;
import com.ipmugo.searchservice.model.CitationScopus;
import com.ipmugo.searchservice.model.Journal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
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

    private Set<Author> authors;

    private CitationScopus citationByScopus;

    private CitationCrossRef citationByCrossRef;

}






package com.ipmugo.articleservice.dto;

import com.ipmugo.articleservice.model.Author;
import com.ipmugo.articleservice.model.CitationCrossRef;
import com.ipmugo.articleservice.model.CitationScopus;
import com.ipmugo.articleservice.model.Journal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleRequest {
    @NotBlank(message = "Journal Id must not be blank")
    private String journalId;

    @NotBlank(message = "Abstract must not be blank")
    private String ojsId;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @NotBlank(message = "Page must not be blank")
    @Size(max = 255, message = "Page should not exceed 255 characters")
    private String pages;

    @NotBlank(message = "Publish Year must not be blank")
    @Size(max = 4, message = "Publish Year should not exceed 4 characters")
    private String publishYear;

    private String lastModifier;

    @NotBlank(message = "Publish Date must not be blank")
    @Size(max = 255, message = "Publish Date should not exceed 255 characters")
    private String publishDate;

    @NotBlank(message = "Publish status must not be blank")
    @Size(max = 255, message = "Publish status should not exceed 255 characters")
    @Field
    private String publishStatus;

    @NotBlank(message = "DOI must not be blank")
    @Size(max = 255, message = "DOI should not exceed 255 characters")
    private String doi;

    @NotBlank(message = "Volume must not be blank")
    @Size(max = 3, message = "Volume should not exceed 3 characters")
    private String volume;

    @NotBlank(message = "Issue must not be blank")
    @Size(max = 3, message = "Issue should not exceed 3 characters")
    private String issue;

    private String copyright;

    @NotBlank(message = "Abstract must not be blank")
    private String abstractText;

    private String fullText;

    @NotBlank(message = "Article PDF must not be blank")
    @Size(max = 255, message = "Article PDF should not exceed 255 characters")
    private String articlePdf;

    @NotEmpty(message = "Keywords must not be empty")
    private List<HashMap<String, String>> keywords;

    @NotEmpty(message = "Authors must not be empty")
    private Set<Author> authors;

    private CitationScopus citationByScopus;

    private CitationCrossRef citationByCrossRef;
}

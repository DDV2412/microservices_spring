package com.ipmugo.journalservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


import com.ipmugo.journalservice.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JournalRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;

    @NotBlank(message = "ISSN must not be blank")
    @Size(max = 10, message = "ISSN should not exceed 10 characters")
    private String issn;

    @NotBlank(message = "E-ISSN must not be blank")
    @Size(max = 10, message = "E-ISSN should not exceed 10 characters")
    private String e_issn;

    @NotBlank(message = "Abbreviation must not be blank")
    @Size(max = 12, message = "Abbreviation should not exceed 12 characters")
    private String abbreviation;


    private String pageImage;

    @NotBlank(message = "Cover image must not be blank")
    @Size(max = 255, message = "Cover image should not exceed 255 characters")
    private String coverImage;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotBlank(message = "Publisher must not be blank")
    @Size(max = 255, message = "Publisher should not exceed 255 characters")
    private String publisher;

    @NotBlank(message = "Journal Site must not be blank")
    @Size(max = 255, message = "Journal Site should not exceed 255 characters")
    private String journalSite;

    @NotBlank(message = "Country must not be blank")
    @Size(max = 32, message = "Country should not exceed 32 characters")
    private String country;

    @NotBlank(message = "Focus Scope must not be blank")
    private String focusScope;

    @NotBlank(message = "Author Guidelines must not be blank")
    private String authorGuidelines;

    @NotBlank(message = "Privacy Statement must not be blank")
    private String privacyStatement;

    private String license;

    private BigDecimal authorFees;

    private String reviewPolice;

    private JournalCitation journalCitationReport;
}

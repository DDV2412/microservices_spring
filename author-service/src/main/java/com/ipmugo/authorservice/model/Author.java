package com.ipmugo.authorservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;


import javax.persistence.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "authors")
@Getter
@Setter
public class Author {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "articleId", nullable = false)
    @JsonBackReference
    private Article article;

    @Column
    private String firstName;

    @Column
    private  String lastName;


    @Column
    private String affiliation;

    @Column
    private String orcid;

    @Column
    private String scopusId;

    @Column
    private String googleScholar;


}

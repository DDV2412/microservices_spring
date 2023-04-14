package com.ipmugo.authorservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "publication")
@Getter
@Setter
public class Publication {

    @Id
    private String id;
    @Column
    private String journal;

    @Column
    private String title;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "author_publications", joinColumns = @JoinColumn(name = "author_id"), inverseJoinColumns = @JoinColumn(name = "publication_id"))
    @JsonBackReference
    private Set<User> authors = new HashSet<>();

    @Column
    private String publishDate;

    @Column
    private String doi;

    @Column
    private String volume;

    @Column
    private String issue;

    @Column
    private String articlePdf;

    @Column
    private int citationByScopus;
    @Column
    private int citationByCrossRef;
    @Column
    private long viewsCount;

    @Column
    private  long downloadCount;

}

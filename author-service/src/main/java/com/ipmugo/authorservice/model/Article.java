package com.ipmugo.authorservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "article")
@Getter
@Setter
public class Article {

    @Id
    private String id;
    @Column
    private String journal;

    @Column
    private String title;



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

    @OneToMany(mappedBy = "article", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Author> authors;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "assignAuthor", joinColumns = @JoinColumn(name = "articleId"), inverseJoinColumns = @JoinColumn(name = "userId"))
    @JsonBackReference
    private Set<User> authorAssign;

    @Column
    private int citationByScopus;
    @Column
    private int citationByCrossRef;
    @Column
    private long viewsCount;
    @Column
    private  long downloadCount;

}

package com.ipmugo.authorservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="userAssign")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

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

    @ManyToMany(mappedBy = "authorAssign", fetch = FetchType.EAGER)
    private Set<Article> articles;

    @Column
    private int hIndex;
}

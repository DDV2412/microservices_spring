package com.ipmugo.authorservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="author")
@Builder
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

    @Column
    private String profile;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    private Set<Publication> publications = new HashSet<>();

    @Column
    private String citation;
}

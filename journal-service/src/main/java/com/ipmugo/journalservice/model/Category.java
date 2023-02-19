package com.ipmugo.journalservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "journalCategory", joinColumns = @JoinColumn(name = "categoryId"), inverseJoinColumns = @JoinColumn(name = "journalId"))
    @JsonBackReference
    private Set<Journal> journals;


    @Column
    @NotBlank(message = "Illustration must not be blank")
    @Size(max = 255, message = "Illustration should not exceed 255 characters")
    private String illustration;

    @Column(unique = true)
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
}

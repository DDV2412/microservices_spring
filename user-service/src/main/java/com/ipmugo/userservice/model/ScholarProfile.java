package com.ipmugo.userservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "scholarProfile")
public class ScholarProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private Integer citations;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference
    private User user;

    @Column
    private Integer hIndex;

    @Column
    private Integer i10Index;
}

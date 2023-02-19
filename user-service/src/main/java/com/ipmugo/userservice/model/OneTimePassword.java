package com.ipmugo.userservice.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OneTimePassword {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private String email;

    @Column(unique = true)
    private Integer otp;

    @Column
    private LocalDateTime expiredAt;

}

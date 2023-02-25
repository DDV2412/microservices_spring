package com.ipmugo.subscribeservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="subscriptions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscribe {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

}

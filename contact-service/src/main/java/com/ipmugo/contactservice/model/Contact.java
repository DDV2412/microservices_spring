package com.ipmugo.contactservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name="contacts")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    @NotBlank(message = "Firstname cannot be blank")
    private String firstName;

    @Column
    @NotBlank(message = "Lastname cannot be blank")
    private String lastName;

    @Column
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Column
    @NotBlank(message = "Company name cannot be blank")
    private String companyName;

    @Column
    @NotBlank(message = "Country cannot be blank")
    private String country;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Message cannot be blank")
    private String message;
}

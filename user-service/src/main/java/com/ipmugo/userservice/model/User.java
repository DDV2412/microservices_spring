package com.ipmugo.userservice.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "FirstName cannot be blank")
    @Column
    private String firstName;

    @NotBlank(message = "LastName cannot be blank")
    @Column
    private  String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Password must be at least 10 characters long")
    @Column
    private String password;

    @Column
    private String affiliation;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column
    private String orcid;

    @Column
    private String scopusId;

    @Column
    private String googleScholar;

    @Column(unique = true)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 10, message = "Username must be at least 10 characters long")
    private String username;

    @Column
    private boolean isVerify;

    @Column
    private String photoProfile;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private boolean isEnabled;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AuthorScholarMetric authorScholarMetric;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AuthorScholarProfile authorScholarProfile;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return roles.stream()
               .map(role -> new SimpleGrantedAuthority(role.getName().name()))
               .collect(Collectors.toList());
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}


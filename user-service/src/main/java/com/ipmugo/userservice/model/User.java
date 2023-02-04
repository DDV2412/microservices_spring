package com.ipmugo.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Document(value = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User implements UserDetails {

    @Id
    private String id;

    @NotBlank(message = "FirstName cannot be blank")
    @Field
    private String firstName;

    @NotBlank(message = "LastName cannot be blank")
    @Field
    private  String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Field
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&])(?=.*[0-9])(?=.*[a-z]).{10,}$", message = "Password must contain at least one uppercase letter, one special character, one number and one lowercase letter, and must be at least 10 characters long")
    @Size(min = 10, message = "Password must be at least 10 characters long")
    @Field
    private String password;

    @Field
    private String affiliation;

    @Field(targetType = FieldType.STRING)
    private String biography;

    @Field
    private String orcid;

    @Field
    private String scopusId;

    @Field
    private String googleScholar;

    @Field
    @Indexed(unique = true)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 10, message = "Username must be at least 10 characters long")
    private String username;

    @Field
    private boolean isVerify;

    @Field
    private String photoProfile;

    @Field
    private List<String> interests;

    @CreatedDate
    private LocalDateTime updatedAt;

    @LastModifiedDate
    private LocalDateTime createdAt;

    private boolean isEnabled;

    @DBRef
    private AuthorScholarMetric authorScholarMetric;

    @DBRef
    private Set<AuthorScholarProfile> authorScholarProfile;

    @DBRef
    private Set<Role> roles = new HashSet<>();

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


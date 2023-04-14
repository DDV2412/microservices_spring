package com.ipmugo.userservice.dto;

import com.ipmugo.userservice.model.ScholarMetric;
import com.ipmugo.userservice.model.ScholarProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.User;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements UserDetails {

    @Id
    private UUID id;

    private String token;

    private String firstName;

    private  String lastName;

    private String affiliation;

    private String biography;

    private String orcid;

    private String scopusId;

    private String googleScholar;

    private String username;

    private boolean isVerify;

    private boolean isEnabled;

    private String photoProfile;

    private List<String> interests;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ScholarMetric> scholarMetrics;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ScholarProfile scholarProfile;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
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

    public UserResponse getBuilder(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .affiliation(user.getAffiliation())
                .biography(user.getBiography())
                .orcid(user.getOrcid())
                .scopusId(user.getScopusId())
                .googleScholar(user.getGoogleScholar())
                .username(user.getUsername())
                .isVerify(user.isVerify())
                .roles(user.getRoles())
                .isEnabled(user.isEnabled())
                .photoProfile(user.getPhotoProfile())
                .scholarMetrics(user.getScholarMetrics())
                .scholarProfile(user.getScholarProfile())
                .build();
    }
}

package com.ipmugo.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements UserDetails {

    @Id
    private String id;

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

    @DBRef
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

    public UserResponse getBuilder(User user, String jwt ){
        return UserResponse.builder()
                .id(user.getId())
                .token(jwt)
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
                .build();
    }
}

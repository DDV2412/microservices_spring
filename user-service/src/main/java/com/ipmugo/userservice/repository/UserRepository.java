package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    Page<User> searchTerm(Pageable pageable, String search);
    List<User> findAllByFirstNameAndLastName(String firstName, String lastName);
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}

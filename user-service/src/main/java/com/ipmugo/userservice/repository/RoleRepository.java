package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(UserRole name);
}

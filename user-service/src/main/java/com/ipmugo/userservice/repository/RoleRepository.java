package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.RoleEnum;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(RoleEnum name);
}

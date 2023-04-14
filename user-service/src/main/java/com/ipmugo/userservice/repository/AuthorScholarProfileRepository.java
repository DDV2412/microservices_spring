package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.ScholarProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorScholarProfileRepository extends JpaRepository<ScholarProfile, UUID> {
}

package com.ipmugo.userservice.repository;

import com.ipmugo.userservice.model.ScholarMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorScholarMetricRepository extends JpaRepository<ScholarMetric, UUID> {
}

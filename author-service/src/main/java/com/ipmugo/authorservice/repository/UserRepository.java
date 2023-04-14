package com.ipmugo.authorservice.repository;


import com.ipmugo.authorservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u, COUNT(p) AS totalPublications, AVG(p.viewsCount) AS avgViewCount, AVG(p.downloadCount) AS avgDownloadCount " +
            "FROM User u LEFT JOIN u.publications p " +
            "GROUP BY u " +
            "ORDER BY u.citation DESC, totalPublications DESC, avgViewCount DESC, avgDownloadCount DESC")
    List<Object[]> findUserByHighestCitationAndMostPublications();

    Page<User> findAll(Pageable pageable);
}
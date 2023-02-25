package com.ipmugo.subscribeservice.repository;



import com.ipmugo.subscribeservice.model.Subscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface SubscribeRepository extends JpaRepository<Subscribe, UUID> {

    Page<Subscribe> findAll(Pageable pageable);

    Optional<Subscribe> findByEmail(String email);

}
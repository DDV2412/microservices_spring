package com.ipmugo.mediaservice.repository;

import com.ipmugo.mediaservice.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {


    Page<Image> findAll(Pageable pageable);

    Optional<Image> findByFileName(String name);
}

package com.ipmugo.contactservice.repository;



import com.ipmugo.contactservice.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Page<Contact> findAll(Pageable pageable);

    Optional<Contact> findByEmail(String email);

}
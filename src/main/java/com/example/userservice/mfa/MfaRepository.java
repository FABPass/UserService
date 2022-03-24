package com.example.userservice.mfa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MfaRepository extends JpaRepository<Mfa, Long> {

    Optional<Mfa> findMfaById(Long id);

}

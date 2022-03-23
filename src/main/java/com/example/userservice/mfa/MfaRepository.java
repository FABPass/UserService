package com.example.userservice.mfa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MfaRepository extends JpaRepository<Mfa, Long> {
}

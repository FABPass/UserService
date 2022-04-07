package com.example.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    @Query("SELECT u FROM User u WHERE u.password.id = ?1")
    Optional<User> findUserByPasswordId(Long passwordId);



}

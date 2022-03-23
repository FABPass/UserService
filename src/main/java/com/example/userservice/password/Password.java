package com.example.userservice.password;

import com.example.userservice.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table

public class Password {
    @Id
    @SequenceGenerator(
            name = "password_sequence",
            sequenceName = "password_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "password_sequence"
    )

    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;


//    @Column(name = "userId", nullable = false)
//    private Long userId;


    public Password() {
    }

    public Password(Long id, String password, LocalDateTime createdAt) {
        this.id = id;
        this.password = password;
        this.createdAt = createdAt;
    }

    public Password(String password, LocalDateTime createdAt) {
        this.password = password;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Password{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

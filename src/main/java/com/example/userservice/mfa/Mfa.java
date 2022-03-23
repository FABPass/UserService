package com.example.userservice.mfa;

import com.example.userservice.user.User;

import javax.persistence.*;

@Entity
@Table

public class Mfa {

    @Id
    @SequenceGenerator(
            name = "mfa_sequence",
            sequenceName = "mfa_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "mfa_sequence"
    )

    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "secret")
    private String secret;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;


//    @Column(name = "userId", nullable = false)
//    private Long userId;

    public Mfa() {
    }

    public Mfa(Long id, String type, String secret, boolean enabled) {
        this.id = id;
        this.type = type;
        this.secret = secret;
        this.enabled = enabled;
    }

    public Mfa(String type, String secret, boolean enabled) {
        this.type = type;
        this.secret = secret;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }






}

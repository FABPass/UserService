package com.example.userservice.user;

import javax.persistence.*;

@Entity
@Table

public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Long passwordId;
    //@Transient  --> na ovaj nacin kazemo da ne kreira u bazi kolonu test
    //private Integer test
    
    public User() {
    }

    public User(Long id, String name, String surname, String email, String phone, Long passwordId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.passwordId = passwordId;
    }

    public User(String name, String surname, String email, String phone, Long passwordId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.passwordId = passwordId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(Long passwordId) {
        this.passwordId = passwordId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passwordId=" + passwordId +
                '}';
    }
}

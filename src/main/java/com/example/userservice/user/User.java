package com.example.userservice.user;

import com.example.userservice.mfa.Mfa;
import com.example.userservice.password.Password;

import javax.persistence.*;

//@Entity(name = "Student") / navedemo kako ce se tabela zvati, ako ne navedemo onda je isto ime kao ime klase
@Entity
@Table(
        name = "user",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "user_phone_unique", columnNames = "phone")
        }
)

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

    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;
    //@Transient  --> na ovaj nacin kazemo da ne kreira u bazi kolonu test
    //private Integer test

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passwordId")
    private Password password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mfaId")
    private Mfa mfa;
    
    public User() {
    }

    public User(Long id, String name, String surname, String email, String phone, Password password, Mfa mfa) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.mfa = mfa;
    }

    public User(String name, String surname, String email, String phone, Password password, Mfa mfa) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.mfa = mfa;
    }

    public User(Long id, String name, String surname, String email, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public User(String name, String surname, String email, String phone) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
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

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Mfa getMfa() {
        return mfa;
    }

    public void setMfa(Mfa mfa) {
        this.mfa = mfa;
    }

    public Long getPasswordId(){
        return password.getId();
    }

    public Long getMfaId(){
        return mfa.getId();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

package com.telerikacademy.web.cryptocurrency_trading_platform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="username")
    private String username;

    @Column(name="password")
    @JsonIgnore
    private String password;

    @Column(name="email")
    @Email
    private String email;

    @Column(name="phone")
    private String phoneNumber;

    @Column(name="balance")
    private double balance;

    @Column(name="isAdmin")
    private boolean isAdmin;

    @Column(name="isBlocked")
    private boolean isBlocked;
}

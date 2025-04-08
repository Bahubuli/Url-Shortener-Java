package com.url.shortener.Vyson.modal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,unique = true)
    private String email;

    @Column()
    private String name;

    @Column(name="api_key",nullable = false, unique = true)
    private String apiKey;

    @Column(name="created_date")
    private Long createdDate;



}

package com.url.shortener.Vyson.modal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UrlData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Auto-incremented numeric ID

    @Column(name="short_url")
    private String shortUrl;
    @Column(name="long_url")
    private String longUrl;
}

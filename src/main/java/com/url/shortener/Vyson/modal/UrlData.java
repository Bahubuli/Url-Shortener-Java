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
    @Column(name="created_date", updatable = false, insertable = false)
    private long createdDate;
    @Column(name="last_accessed_date")
    private Long lastAccessedDate;
    @Column(name="visit_count")
    private int visitCount;
}

package com.url.shortener.Vyson.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlDataDTO {
    private Long id;
    private String shortUrl;
    private String longUrl;
    private long createdDate;
    private Long lastAccessedDate;
    private int visitCount;
    private Boolean active;
    private Long expiryDate;

    public UrlDataDTO(Long id, String shortUrl, String longUrl, long createdDate, Long lastAccessedDate, int visitCount, Boolean active, Long expiryDate) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.createdDate = createdDate;
        this.lastAccessedDate = lastAccessedDate;
        this.visitCount = visitCount;
        this.active = active;
        this.expiryDate = expiryDate;
    }
}
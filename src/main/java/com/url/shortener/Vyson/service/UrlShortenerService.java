package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Transactional
    public String GenerateShortCode(String longUrl) {

        UrlData urlData = new UrlData();
        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(" ");

        UrlData saved = urlRepository.save(urlData);

        String shortCode = base62Encoder.encode(saved.getId());
        urlData.setShortUrl(shortCode);

        urlRepository.save(urlData);

        return shortCode;

    }

    public String getLongUrl(String shortCode) {
        UrlData urlData = urlRepository.findByShortUrl(shortCode);
        return (urlData != null) ? urlData.getLongUrl() : null;

    }

}

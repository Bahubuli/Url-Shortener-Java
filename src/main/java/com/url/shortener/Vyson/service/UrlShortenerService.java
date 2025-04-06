package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.exception.DuplicateUrlException;
import com.url.shortener.Vyson.exception.NotFoundException;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Transactional
    public String GenerateShortCode(String longUrl) {

        Optional<UrlData> existing = Optional.ofNullable(urlRepository.findByLongUrl(longUrl));
        if(existing.isPresent()){
           throw new DuplicateUrlException(longUrl + " already exists");
        }
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
    @Transactional
    public String deleteLongUrl(String longUrl) {
        int deletedCount = urlRepository.deleteByLongUrl(longUrl);
        if(deletedCount==0)
            throw new NotFoundException("given url does not exist");;

        return "Deleted " + deletedCount + " urls";
    }

}

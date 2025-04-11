package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.exception.DuplicateUrlException;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TransactionalUrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private Base62Encoder base62Encoder;

    /**
     * This method creates a new UrlData entry and generates a short code.
     * The whole process is executed within a transaction.
     *
     * @param longUrl      the original URL
     * @param user         the user creating the short URL
     * @param expiryDate   optional expiry date for the URL
     * @param userShortCode optional short code provided by the user
     * @return the generated short code
     */
    @Transactional
    public String shortenUrlTransactional(String longUrl, User user, Instant expiryDate, String userShortCode) {
        // If a custom short code is provided, check for duplication.
        if (userShortCode != null) {
            UrlData existingUrlData = urlRepository.findByShortUrl(userShortCode);
            if (existingUrlData != null && existingUrlData.getUser().getId().equals(user.getId())) {
                throw new DuplicateUrlException("This short code is already in use");
            }
        }

        // Create a new UrlData instance and set initial properties.
        UrlData urlData = new UrlData();
        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(" ");  // temporary value
        urlData.setUser(user);

        if (expiryDate != null) {
            urlData.setExpiryDate(expiryDate.toEpochMilli());
        }

        // Save the new UrlData to generate an ID.
        UrlData saved = urlRepository.save(urlData);

        String shortCode = userShortCode;
        if (userShortCode != null) {
            urlData.setShortUrl(userShortCode);
        } else {
            // Generate the short code using the Base62 encoder.
            shortCode = base62Encoder.encode(saved.getId());
            urlData.setShortUrl(shortCode);
        }

        // Save again to update the short URL.
        urlRepository.save(urlData);

        return shortCode;
    }
}

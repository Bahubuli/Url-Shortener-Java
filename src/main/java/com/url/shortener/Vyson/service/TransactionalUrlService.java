package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.exception.DuplicateUrlException;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String shortenUrlTransactional(String longUrl, User user,
                                          Instant expiryDate, String userShortCode) {
        // Handle custom short code
        if (userShortCode != null) {
            return handleCustomShortCode(longUrl, user, expiryDate, userShortCode);
        }
        // Handle system-generated short code
        return handleSystemGeneratedCode(longUrl, user, expiryDate);
    }

    private String handleCustomShortCode(String longUrl, User user,
                                         Instant expiryDate, String userShortCode) {
        // Check for duplicate short URL
        if (urlRepository.findByShortUrl(userShortCode) != null) {
            throw new DuplicateUrlException("This short code is already in use");
        }

        // Decode custom short code to get desired ID
        Long desiredId = Base62Encoder.decode(userShortCode);

        // Check if ID is already taken
        if (urlRepository.existsById(String.valueOf(desiredId))) {
            throw new DuplicateUrlException("This short code conflicts with system ID space");
        }

        // Create entity with custom ID
        UrlData urlData = createUrlData(desiredId, userShortCode, longUrl, user, expiryDate);

        // Adjust the sequence to ensure system-generated IDs will not collide in the future.
        //adjustSequence(desiredId + 1);

        entityManager.persist(urlData);
        return userShortCode;
    }

    private String handleSystemGeneratedCode(String longUrl, User user, Instant expiryDate) {
        Long nextId;
        // Loop until an available ID is found
        while (true) {
            // Get next ID from sequence
            nextId = urlRepository.getNextIdSequence();
            // If this ID already exists, adjust the sequence and try again
            if (urlRepository.existsById(String.valueOf(nextId))) {
                adjustSequence(nextId + 1);
            } else {
                break;
            }
        }

        String shortCode = base62Encoder.encode(nextId);

        // Create entity with system-generated ID
        UrlData urlData = createUrlData(nextId, shortCode, longUrl, user, expiryDate);
        entityManager.persist(urlData);
        return shortCode;
    }

    private UrlData createUrlData(Long id, String shortCode, String longUrl,
                                  User user, Instant expiryDate) {
        UrlData urlData = new UrlData();
        urlData.setId(id);
        urlData.setShortUrl(shortCode);
        urlData.setLongUrl(longUrl);
        urlData.setUser(user);

        if (expiryDate != null) {
            urlData.setExpiryDate(expiryDate.toEpochMilli());
        }
        return urlData;
    }

    private void adjustSequence(Long nextVal) {
        entityManager.createNativeQuery(
                        "ALTER SEQUENCE url_data_id_seq RESTART WITH " + nextVal)
                .executeUpdate();
    }

}


package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.exception.DuplicateUrlException;
import com.url.shortener.Vyson.exception.ExpiredException;
import com.url.shortener.Vyson.exception.NotFoundException;
import com.url.shortener.Vyson.exception.UnauthorizedException;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Transactional
    public String GenerateShortCode(String longUrl, User user, Instant expiryDate) {

//        Optional<UrlData> existing = Optional.ofNullable(urlRepository.findByLongUrl(longUrl));
//        if(existing.isPresent()){
//           throw new DuplicateUrlException(longUrl + " already exists");
//        }
        UrlData urlData = new UrlData();
        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(" ");
        urlData.setUser(user);

        if(expiryDate!=null){
            urlData.setExpiryDate(expiryDate.toEpochMilli());
        }

        UrlData saved = urlRepository.save(urlData);

        String shortCode = base62Encoder.encode(saved.getId());
        urlData.setShortUrl(shortCode);

        urlRepository.save(urlData);

        return shortCode;

    }

    @Transactional
    public String getLongUrl(String shortCode) {
        UrlData urlData = urlRepository.findByShortUrl(shortCode);

        // validate url
        if(urlData==null){ return null; }

        // validate the expiry date
        Long expiryDate = urlData.getExpiryDate();
        if(expiryDate!=null)
        {
            long now = Instant.now().toEpochMilli();
            if(now>expiryDate)
               throw new ExpiredException("your shortened url is expired");
        }

            urlData.setVisitCount(urlData.getVisitCount()+1);

            urlData.setLastAccessedDate(System.currentTimeMillis());

            urlRepository.save(urlData);

            return urlData.getLongUrl();

    }
    @Transactional
    public String deleteLongUrl(String longUrl,User user) {

        List<UrlData> urlDataList  = urlRepository.findByLongUrlAndUser(longUrl,user);

        if(urlDataList.isEmpty())
            throw new NotFoundException("given url does not exist");

        for(UrlData urlData : urlDataList){
            urlData.setIsDeleted(true);
            urlRepository.save(urlData);
        }

        return "Your Url is deleted";
    }

}

package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.exception.DuplicateUrlException;
import com.url.shortener.Vyson.exception.NotFoundException;
import com.url.shortener.Vyson.exception.UnauthorizedException;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UrlRepository;
import com.url.shortener.Vyson.utils.Base62Encoder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Transactional
    public String GenerateShortCode(String longUrl, User user) {

//        Optional<UrlData> existing = Optional.ofNullable(urlRepository.findByLongUrl(longUrl));
//        if(existing.isPresent()){
//           throw new DuplicateUrlException(longUrl + " already exists");
//        }
        UrlData urlData = new UrlData();
        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(" ");
        urlData.setUser(user);

        UrlData saved = urlRepository.save(urlData);

        String shortCode = base62Encoder.encode(saved.getId());
        urlData.setShortUrl(shortCode);

        urlRepository.save(urlData);

        return shortCode;

    }

    @Transactional
    public String getLongUrl(String shortCode) {
        UrlData urlData = urlRepository.findByShortUrl(shortCode);

        if(urlData!=null){
            urlData.setVisitCount(urlData.getVisitCount()+1);

            urlData.setLastAccessedDate(System.currentTimeMillis());

            urlRepository.save(urlData);

            return urlData.getLongUrl();
        }
        return null;

    }
    @Transactional
    public String deleteLongUrl(String longUrl,User user) {

        List<UrlData> urlData  = urlRepository.findByLongUrlAndUser(longUrl,user);

        if(urlData)
            throw new NotFoundException("given url does not exist");

        if(!urlData.getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("you are not authorized to delete this url");

        urlData.setIsDeleted(true);
        urlRepository.save(urlData);

        return "Your Url is deleted";
    }

}

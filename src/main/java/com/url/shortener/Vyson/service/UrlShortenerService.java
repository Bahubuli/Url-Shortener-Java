package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.dto.UrlRequest;
import com.url.shortener.Vyson.dto.UrlResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Autowired
    public TransactionalUrlService transactionalUrlService;

    @Transactional
    public String GenerateShortCode(String longUrl, User user, Instant expiryDate, String userShortCode) {
        return transactionalUrlService.shortenUrlTransactional(longUrl, user, expiryDate, userShortCode);
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
    public String deleteShortCode(String shortCode,User user) {

        List<UrlData> urlDataList  = urlRepository.findByShortUrlAndUser(shortCode,user);

        if(urlDataList.isEmpty())
            throw new NotFoundException("given short code does not exist");

        for(UrlData urlData : urlDataList){
            urlData.setIsDeleted(true);
            urlRepository.save(urlData);
        }

        return "Your Url is deleted";
    }

    public List<UrlResponse> shortenUrlsInBatch(List<UrlRequest> requests, User user) {
        if(user.getTier().equals("hobby"))
            throw new UnauthorizedException("Please upgrade to business tier in order to bulk shortening operation");
        List<UrlResponse> responses = new ArrayList<>();
        for (UrlRequest req : requests) {
            try {

                String shortUrl = GenerateShortCode(req.getLongUrl(), user, req.getExpiryDate(), req.getShortCode());

                responses.add(new UrlResponse(req.getLongUrl(), shortUrl, true, null));
            } catch (Exception e) {
                // In case of any error, create an error response.
                UrlResponse.ErrorResponse errorResponse = new UrlResponse.ErrorResponse("ERR001", e.getMessage());
                responses.add(new UrlResponse(req.getLongUrl(), null, false, errorResponse));
            }
        }
        return responses;
    }
}

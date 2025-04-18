package com.url.shortener.Vyson.service;

import com.url.shortener.Vyson.dto.UrlDataDTO;
import com.url.shortener.Vyson.dto.UrlRequest;
import com.url.shortener.Vyson.dto.UrlResponse;
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

@Service
public class UrlShortenerService {

    @Autowired
    public UrlRepository urlRepository;
    @Autowired
    public Base62Encoder base62Encoder;

    @Autowired
    public TransactionalUrlService transactionalUrlService;


    public UrlData GenerateShortCode(String longUrl, User user, Instant expiryDate, String userShortCode) {
        return transactionalUrlService.shortenUrlTransactional(longUrl, user, expiryDate, userShortCode);
    }
    @Transactional
    public String getLongUrl(String shortCode,String password) {
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
        String existingPassword = urlData.getPassword();
        System.out.println("existingPassword: "+existingPassword);
        if(existingPassword!=null && !existingPassword.equals(password))
            throw new UnauthorizedException("your password is incorrect");

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

        int isDeleted = urlRepository.deleteByShortUrlAndUser(shortCode,user);

        return isDeleted!=0 ?  "Your Url is deleted" : "delete operation failed";
    }

    public List<UrlResponse> shortenUrlsInBatch(List<UrlRequest> requests, User user) {
        if(user.getTier().equals("hobby"))
            throw new UnauthorizedException("Please upgrade to business tier in order to bulk shortening operation");
        List<UrlResponse> responses = new ArrayList<>();
        for (UrlRequest req : requests) {
            try {

                UrlData urlData = GenerateShortCode(req.getLongUrl(), user, req.getExpiryDate(), req.getShortCode());

                responses.add(new UrlResponse(String.valueOf(urlData.getId()),urlData.getLongUrl(), urlData.getShortUrl(), true, null));
            } catch (Exception e) {
                // In case of any error, create an error response.
                UrlResponse.ErrorResponse errorResponse = new UrlResponse.ErrorResponse("ERR001", e.getMessage());
                responses.add(new UrlResponse(null,req.getLongUrl(), null, false, errorResponse));
            }
        }
        return responses;
    }

    @Transactional
    public UrlData updateUrlData(Long id,String longUrl, User user, Instant expiryDate, String userShortCode,Boolean active) {
        List<UrlData> oldUrlDataList = urlRepository.findByIdAndUser(id,user);
        if(oldUrlDataList.isEmpty())
            throw new NotFoundException("given id does not exist");
        UrlData oldUrlData = oldUrlDataList.get(0);


        if(userShortCode!=null)
        {
            UrlData urlData= transactionalUrlService.shortenUrlTransactional(longUrl, user, expiryDate, userShortCode);
            List<UrlData> updatedUrlDataList = urlRepository.findByShortUrlAndUser(urlData.getShortUrl(),user);
            if(active!=null)
            {
                UrlData updatedUrlData = updatedUrlDataList.get(0);
                updatedUrlData.setActive(active);
            }
            String oldShortCode = oldUrlData.getShortUrl();
            int isDeleted = urlRepository.deleteByShortUrlAndUser(oldShortCode,user);
            return urlData;
        }
        else
        {
            if(longUrl!=null) oldUrlData.setLongUrl(longUrl);
            if(expiryDate!=null)   oldUrlData.setExpiryDate(expiryDate.toEpochMilli());
            if(active!=null)   oldUrlData.setActive(active);
            urlRepository.save(oldUrlData);
            return oldUrlData;
        }
        //return transactionalUrlService.updateUrlTransactional(id,longUrl, user, expiryDate, userShortCode);
    }

    public List<UrlDataDTO> getAllUrls(User user) {
        return urlRepository.findAllByUserId(user.getId());
    }
}

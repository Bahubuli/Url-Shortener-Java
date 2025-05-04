package com.url.shortener.Vyson.controllers;

import com.url.shortener.Vyson.dto.*;
import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.url.shortener.Vyson.service.UrlShortenerService;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UrlController {

@Autowired
private UrlShortenerService urlShortenerService;

@Autowired
private UserRepository userRepository;

@PostMapping("/shorten")
public ResponseEntity<UrlResponse> shortenUrl(HttpServletRequest servletRequest,
                         @Valid @RequestBody UrlRequest req) {

   User user = (User) servletRequest.getAttribute("user");

   String longUrl = req.getLongUrl();
   String userShortCode = req.getShortCode();
   Instant expiryDate = req.getExpiryDate();
   UrlData urlData = urlShortenerService.GenerateShortCode(longUrl,user,expiryDate,userShortCode);
   UrlResponse response =  new UrlResponse(String.valueOf(urlData.getId()),longUrl,urlData.getShortUrl(),urlData.getActive(),true,null);
   return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

@GetMapping("/redirect")
   public ResponseEntity<?> redirect(@RequestParam("code") String shortCode,@RequestParam(name="password",  required = false) String password) {
      String longUrl = urlShortenerService.getLongUrl(shortCode,password);
      if(longUrl!=null)
      {
         return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL Not Found");
}
@DeleteMapping("/delete")
   public ResponseEntity<?> deleteShortCode(@RequestParam("shortCode") String shortCode, HttpServletRequest servletRequest) {

   User user = (User) servletRequest.getAttribute("user");

      String response = urlShortenerService.deleteShortCode(shortCode,user);
      return ResponseEntity.status(HttpStatus.OK).body(response);

}

@PostMapping("/shorten/batch")
public ResponseEntity<BatchUrlResponse> shortenInBatch(@Valid @RequestBody BatchUrlRequest req,
                                                       HttpServletRequest servletRequest) {
   User user = (User) servletRequest.getAttribute("user");

   List<UrlResponse> results = urlShortenerService.shortenUrlsInBatch(req.getUrls(), user);

   // Calculate the counts based on each response's success flag
   int successCount = (int) results.stream().filter(UrlResponse::isSuccess).count();
   int errorCount = results.size() - successCount;

   // Build and populate the BatchUrlResponse
   BatchUrlResponse response = new BatchUrlResponse();
   response.setResults(results);
   response.setSuccessCount(successCount);
   response.setErrorCount(errorCount);

   return ResponseEntity.status(HttpStatus.OK).body(response);
}

   @PutMapping("/urlData")
   public ResponseEntity<UrlResponse> updateUrlData(
           @Valid @RequestBody UpdateUrlDataRequest req,
           HttpServletRequest servletRequest) {

      User user = (User) servletRequest.getAttribute("user");

      Long id = req.getId();
      String longUrl = req.getLongUrl();
      String userShortCode = req.getShortCode();
      Instant expiryDate = req.getExpiryDate();
      Boolean active = req.getActive();

      // Update the URL data and get the new short code
      UrlData urlData = urlShortenerService.updateUrlData(id, longUrl, user, expiryDate, userShortCode, active);
      System.out.println("urlData from service "+urlData);
      // Create the response object; here we're reusing the same UrlResponse structure
      UrlResponse response = new UrlResponse(String.valueOf(urlData.getId()),longUrl, urlData.getShortUrl(),urlData.getActive(),true, null);

      // Return the response with HTTP status 200 OK
      return ResponseEntity.ok(response);
   }

@GetMapping("/allUrls")
   public List<UrlDataDTO>GetAllUrls(HttpServletRequest servletRequest) {

   User user = (User) servletRequest.getAttribute("user");

   return urlShortenerService.getAllUrls(user);
}

}

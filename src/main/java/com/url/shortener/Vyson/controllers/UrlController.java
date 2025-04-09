package com.url.shortener.Vyson.controllers;

import com.url.shortener.Vyson.dto.UrlRequest;
import com.url.shortener.Vyson.modal.User;
import com.url.shortener.Vyson.repo.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.url.shortener.Vyson.service.UrlShortenerService;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@RestController
public class UrlController {

@Autowired
private UrlShortenerService urlShortenerService;

@Autowired
private UserRepository userRepository;

@PostMapping("/shorten")
public String shortenUrl(@RequestHeader(value="api_key", required = false) String api_key,
                         @Valid @RequestBody UrlRequest req)
{

   User user = userRepository.findByApiKey(api_key)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

   String longUrl = req.getLongUrl();
   Instant expiryDate = req.getExpiryDate();
   String shortCode = urlShortenerService.GenerateShortCode(longUrl,user,expiryDate);
   return shortCode;
}

@GetMapping("/redirect")
   public ResponseEntity<?> redirect(@RequestParam("code") String shortCode) {
      String longUrl = urlShortenerService.getLongUrl(shortCode);
      System.out.println("longUrl: ######################################################## " + longUrl);
      if(longUrl!=null)
      {
         return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL Not Found");
}
@DeleteMapping("/delete")
   public ResponseEntity<?> deleteLongUrl(@RequestParam("longUrl") String longUrl,@RequestHeader(value="api_key", required = false) String api_key) {

   User user = userRepository.findByApiKey(api_key)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

      String response = urlShortenerService.deleteLongUrl(longUrl,user);
      return ResponseEntity.status(HttpStatus.OK).body(response);

}



}

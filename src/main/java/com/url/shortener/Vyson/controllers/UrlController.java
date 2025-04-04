package com.url.shortener.Vyson.controllers;

import com.url.shortener.Vyson.dto.UrlRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.url.shortener.Vyson.service.UrlShortenerService;

import java.net.URI;

@RestController
public class UrlController {

@Autowired
private UrlShortenerService urlShortenerService;

@PostMapping("/shorten")
public String shortenUrl(@Valid @RequestBody UrlRequest req)
{
   String longUrl = req.getLongUrl();
   String shortCode = urlShortenerService.GenerateShortCode(longUrl);
   return shortCode;
}

@GetMapping("/redirect")
   public ResponseEntity<?> redirect(@RequestParam("code") String shortCode) {

   try {
      String longUrl = urlShortenerService.getLongUrl(shortCode);
      System.out.println("longUrl: ######################################################## " + longUrl);
      if(longUrl!=null)
      {
         return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL Not Found");
   }
   catch (Exception e) {
      System.out.println("---> error is ---> " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
   }

}



}

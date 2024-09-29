package com.urlshortener.urlshortenerbackend.controller;

import com.urlshortener.urlshortenerbackend.dto.DeleteUrlResponseDto;
import com.urlshortener.urlshortenerbackend.dto.ErrorResponseDto;
import com.urlshortener.urlshortenerbackend.model.Url;
import com.urlshortener.urlshortenerbackend.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        String shortUrl = urlService.shortenUrl(longUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> redirectToLongUrl(@PathVariable String shortUrl) {
        String longUrl = urlService.getOriginalUrl(shortUrl);
        if (longUrl != null) {
            return ResponseEntity.ok(longUrl);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<?> deleteShortUrl(@PathVariable String shortUrl, HttpServletResponse response)throws IOException {
        Url urlToDelete = urlService.getUrlByShortUrl(shortUrl);
        if (urlToDelete == null) {
            ErrorResponseDto errorResponse = new ErrorResponseDto("404","URL does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
        urlService.deleteUrlByShortUrl(shortUrl);
        DeleteUrlResponseDto data = new DeleteUrlResponseDto("Short URL has been successfully deleted.");
        return ResponseEntity.ok(data);
    }
}

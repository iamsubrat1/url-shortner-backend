package com.urlshortener.urlshortenerbackend.controller;

import com.urlshortener.urlshortenerbackend.dto.ShortenUrlRequest;
import com.urlshortener.urlshortenerbackend.dto.ShortenUrlResponse;
import com.urlshortener.urlshortenerbackend.model.Url;
import com.urlshortener.urlshortenerbackend.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/urls")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * Creates a short URL for a given long URL.
     */
    @PostMapping
    public ResponseEntity<ShortenUrlResponse> createShortUrl(
            @Valid @RequestBody ShortenUrlRequest request) {

        Url url = urlShortenerService.createShortUrl(request.longUrl(), request.customCode(),
                request.expiresAt());

        ShortenUrlResponse response = new ShortenUrlResponse(url.getShortUrl(), url.getExpiresAt(),
                url.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Redirects short URL to original long URL.
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrl(
            @PathVariable String shortCode) {

        String longUrl = urlShortenerService.resolveShortUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }

    /**
     * Deletes a short URL.
     */
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        urlShortenerService.deleteShortUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}

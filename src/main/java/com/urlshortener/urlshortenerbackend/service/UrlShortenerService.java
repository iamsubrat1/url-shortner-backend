package com.urlshortener.urlshortenerbackend.service;

import com.urlshortener.urlshortenerbackend.model.Url;
import com.urlshortener.urlshortenerbackend.repository.UrlMappingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final String BASE62 =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    private final UrlMappingRepository urlMappingRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    /**
     * Creates or returns an existing short code for a long URL.
     * Ensures idempotency and collision safety.
     */
    public String createShortUrl(String longUrl) {

        Optional<Url> existing = urlMappingRepository.findByLongUrl(longUrl);
        if (existing.isPresent()) {
            return existing.get().getShortUrl();
        }

        String shortCode = generateUniqueShortCode();

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortCode);
        url.setCreatedAt(LocalDateTime.now());

        urlMappingRepository.save(url);
        return shortCode;
    }

    /**
     * Resolves a short code to original URL.
     */
    public String resolveShortUrl(String shortCode) {
        return urlMappingRepository.findByShortUrl(shortCode)
                .map(Url::getLongUrl)
                .orElseThrow(() ->
                        new IllegalArgumentException("Short URL not found"));
    }

    /**
     * Deletes a short URL.
     */
    @Transactional
    public void deleteShortUrl(String shortCode) {
        if (!urlMappingRepository.existsByShortUrl(shortCode)) {
            throw new IllegalArgumentException("Short URL not found");
        }
        urlMappingRepository.deleteByShortUrl(shortCode);
    }

    /**
     * Generates collision-safe short codes.
     */
    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlMappingRepository.existsByShortUrl(shortCode));

        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(BASE62.charAt(secureRandom.nextInt(BASE62.length())));
        }
        return sb.toString();
    }
}

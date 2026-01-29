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
     * Creates a short URL with optional custom code and expiration.
     * Ensures idempotency, validation, and collision safety.
     */
    public Url createShortUrl(String longUrl, String customCode, LocalDateTime expiresAt) {

        // Validate expiration
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration time must be in the future");
        }

        // Handle custom short code
        String shortCode;
        if (customCode != null && !customCode.isBlank()) {
            if (urlMappingRepository.existsByShortUrl(customCode)) {
                throw new IllegalStateException("Custom short code already exists");
            }
            shortCode = customCode;
        } else {
            // Idempotency only when system generates code
            Optional<Url> existing = urlMappingRepository.findByLongUrl(longUrl);
            if (existing.isPresent()) {
                return existing.get();
            }
            shortCode = generateUniqueShortCode();
        }

        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(expiresAt);

        return urlMappingRepository.save(url);
    }

    /**
     * Resolves short code to original URL.
     * Enforces expiration rules.
     */
    public String resolveShortUrl(String shortCode) {
        Url url = urlMappingRepository.findByShortUrl(shortCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("Short URL not found"));

        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short URL has expired");
        }

        return url.getLongUrl();
    }

    /**
     * Deletes a short URL safely.
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

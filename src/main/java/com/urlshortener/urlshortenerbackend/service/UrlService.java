package com.urlshortener.urlshortenerbackend.service;

import com.urlshortener.urlshortenerbackend.model.Url;
import com.urlshortener.urlshortenerbackend.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String shortenUrl(String longUrl) {
/*     Maintaining idempotency by checking if the same longUrl already exist in our db.If it exists then returning the existing
       sortUrl for that and if not creating a new one. */
        Optional<Url> existingUrl = urlRepository.findByLongUrl(longUrl);

        if (existingUrl.isPresent()) {
            return existingUrl.get().getShortUrl();
        }

        String shortUrl = generateShortUrl();
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);
        url.setCreatedAt(LocalDateTime.now());
        urlRepository.save(url);
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        return (url != null) ? url.getLongUrl() : null;
    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortUrl = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString();
    }

    public Url getUrlByShortUrl(String shortUrl) {
        Url urlObj = urlRepository.findByShortUrl(shortUrl);
        return urlObj;
    }

    @Transactional
    public void deleteUrlByShortUrl(String shortUrl) {
        urlRepository.deleteByShortUrl(shortUrl);
    }
}

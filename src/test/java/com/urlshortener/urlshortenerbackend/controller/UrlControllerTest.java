package com.urlshortener.urlshortenerbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlshortener.urlshortenerbackend.model.Url;
import com.urlshortener.urlshortenerbackend.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @Mock
    private HttpServletResponse httpServletResponse;

    private MockMvc mockMvc;

    @Autowired
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        urlController = new UrlController(urlService);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    // Helper method to convert objects to JSON string
    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Shorten URL - Success
    @Test
    void testShortenUrl_Success() throws Exception {
        String longUrl = "https://www.example.com";
        String expectedShortUrl = "abc123";

        when(urlService.shortenUrl(longUrl)).thenReturn(expectedShortUrl);

        mockMvc.perform(post("/api/url/shorten")
                        .content(longUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedShortUrl));

        verify(urlService, times(1)).shortenUrl(longUrl);
    }

    //Redirect to Long URL - Success
    @Test
    void testRedirectToLongUrl_Success() throws Exception {
        String shortUrl = "abc123";
        String longUrl = "https://www.example.com";

        when(urlService.getOriginalUrl(shortUrl)).thenReturn(longUrl);

        mockMvc.perform(get("/api/url/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(longUrl));

        verify(urlService, times(1)).getOriginalUrl(shortUrl);
    }

    //Redirect to Long URL - Not Found
    @Test
    void testRedirectToLongUrl_NotFound() throws Exception {
        String shortUrl = "abc123";

        when(urlService.getOriginalUrl(shortUrl)).thenReturn(null);

        mockMvc.perform(get("/api/url/{shortUrl}", shortUrl))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).getOriginalUrl(shortUrl);
    }


    //Delete Short URL - Success
    @Test
    void testDeleteShortUrl_Success() throws Exception {
        String shortUrl = "abc123";
        Url url = new Url();
        when(urlService.getUrlByShortUrl(shortUrl)).thenReturn(url);

        mockMvc.perform(delete("/api/url/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Short URL has been successfully deleted."));

        verify(urlService, times(1)).deleteUrlByShortUrl(shortUrl);
    }

    //Delete Short URL - Not Found
    @Test
    void testDeleteShortUrl_NotFound() throws Exception {
        String shortUrl = "nonexistent";

        when(urlService.getUrlByShortUrl(shortUrl)).thenReturn(null);

        mockMvc.perform(delete("/api/url/{shortUrl}", shortUrl))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("URL does not exist"));

        verify(urlService, never()).deleteUrlByShortUrl(shortUrl);
    }
}
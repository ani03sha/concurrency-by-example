package org.redquark.concurrency.problems.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redquark.concurrency.problems.urlshortener.encoders.Base62Encoder;
import org.redquark.concurrency.problems.urlshortener.encoders.Encoder;
import org.redquark.concurrency.problems.urlshortener.repositories.InMemoryUrlRepository;
import org.redquark.concurrency.problems.urlshortener.repositories.UrlRepository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class UrlShortenerServiceTest {

    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        UrlRepository urlRepository = new InMemoryUrlRepository();
        Encoder encoder = new Base62Encoder();
        service = new UrlShortenerService(urlRepository, encoder);
    }

    @Test
    void shouldReturnSameShortCodeForConcurrentSameLongUrlRequests() {
        final int threadCount = 50;
        final String longUrl = "https://test.com/a/b/c?x=1";
        try (final ExecutorService executors = Executors.newFixedThreadPool(threadCount)) {
            final Set<String> shortCodes = ConcurrentHashMap.newKeySet();
            final CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executors.submit(() -> {
                    try {
                        String shortCode = service.shortenUrl(longUrl);
                        shortCodes.add(shortCode);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executors.shutdown();
            assertEquals(1, shortCodes.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldGenerateDifferentShortCodesForConcurrentDifferentLongUrls() {
        final int threadCount = 50;
        final String longUrl = "https://test.com/a/b/c";
        try (final ExecutorService executors = Executors.newFixedThreadPool(threadCount)) {
            final Set<String> shortCodes = ConcurrentHashMap.newKeySet();
            final CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                int finalI = i;
                executors.submit(() -> {
                    try {
                        String shortCode = service.shortenUrl(longUrl + "?x=" + finalI);
                        shortCodes.add(shortCode);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executors.shutdown();
            assertEquals(threadCount, shortCodes.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldRetrieveOriginalUrlsConcurrently() {
        final int threadCount = 50;
        try (final ExecutorService executors = Executors.newFixedThreadPool(threadCount)) {
            final Set<String> longUrls = ConcurrentHashMap.newKeySet();
            final CountDownLatch latch = new CountDownLatch(threadCount);

            // Pre-register Urls
            for (int i = 0; i < threadCount; i++) {
                service.shortenUrl("https://test.com/a/b/c?x=" + i);
            }

            for (int i = 0; i < threadCount; i++) {
                int finalI = i;
                executors.submit(() -> {
                    try {
                        String shortCode = service.shortenUrl("https://test.com/a/b/c?x=" + finalI);
                        String originalUrl = service.getOriginalUrl(shortCode);
                        longUrls.add(originalUrl);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executors.shutdown();
            assertEquals(threadCount, longUrls.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
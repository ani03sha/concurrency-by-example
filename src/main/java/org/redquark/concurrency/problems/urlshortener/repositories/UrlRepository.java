package org.redquark.concurrency.problems.urlshortener.repositories;

import org.redquark.concurrency.problems.urlshortener.domains.UrlMapping;

import java.util.Optional;

public interface UrlRepository {

    Optional<String> findShortcode(String longUrl);
    Optional<String> findLongUrl(String shortCode);
    boolean saveIfAbsent(UrlMapping urlMapping);
}

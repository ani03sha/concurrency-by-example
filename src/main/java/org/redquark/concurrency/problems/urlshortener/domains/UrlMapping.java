package org.redquark.concurrency.problems.urlshortener.domains;

public class UrlMapping {

    private final String longUrl;
    private final String shortCode;

    public UrlMapping(String getLongUrl, String shortCode) {
        this.longUrl = getLongUrl;
        this.shortCode = shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getShortCode() {
        return shortCode;
    }
}

package org.redquark.concurrency.problems.urlshortener.encoders;

public class Base62Encoder implements Encoder {

    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String encode(long number) {
        final StringBuilder sb = new StringBuilder();
        while (number > 0) {
            final int remainder = (int) (number % 62);
            sb.append(CHARACTERS.charAt(remainder));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}

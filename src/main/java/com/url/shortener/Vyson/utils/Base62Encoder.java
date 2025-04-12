package com.url.shortener.Vyson.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = CHARSET.length();

    // Private constructor to prevent instantiation
    private Base62Encoder() {
    }

    public static String encode(long seqId) {
        if(seqId==0) return String.valueOf(CHARSET.charAt(0));

        StringBuilder sb = new StringBuilder();

        while(seqId>0)
        {
            sb.append(CHARSET.charAt((int)(seqId %62)));
            seqId=seqId/BASE;
        }
        return sb.reverse().toString();
    }
    public static long decode(String encodedString) {
        long result = 0;
        long power = 1;

        for (int i = encodedString.length() - 1; i >= 0; i--) {
            char c = encodedString.charAt(i);
            int digit = CHARSET.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character in Base62 string: " + c);
            }
            result += digit * power;
            power *= BASE;
        }
        return result;
    }
}

package com.url.shortener.Vyson.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    private static final String CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // Private constructor to prevent instantiation
    private Base62Encoder() {
    }

    public static String encode(long seqId) {
        if(seqId==0) return String.valueOf(CHARSET.charAt(0));

        StringBuilder sb = new StringBuilder();

        while(seqId>0)
        {
            sb.append(CHARSET.charAt((int)(seqId %62)));
            seqId=seqId/62;
        }
        return sb.reverse().toString();
    }
}

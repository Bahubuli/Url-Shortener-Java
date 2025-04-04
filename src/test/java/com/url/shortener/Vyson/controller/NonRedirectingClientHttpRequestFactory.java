package com.url.shortener.Vyson.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class NonRedirectingClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    @Override
    protected HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        HttpURLConnection connection = super.openConnection(url, proxy);
        connection.setInstanceFollowRedirects(false); // disable automatic redirects
        return connection;
    }
}

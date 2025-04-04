package com.url.shortener.Vyson.dto; // Add package declaration
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Invalid URL format")
    private String longUrl;

    // Getter and Setter
    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
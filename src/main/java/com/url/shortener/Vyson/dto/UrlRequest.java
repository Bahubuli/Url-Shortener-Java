package com.url.shortener.Vyson.dto; // Add package declaration
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            message = "Invalid URL format")
    private String longUrl;

    @Nullable // Indicates optional (Spring-specific, optional)
    @Size(min = 1, message = "Short code must not be empty if provided") // Ensures non-blank if present
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$",
            message = "Short code can only contain letters, numbers, hyphens, or underscores")
    private String shortCode; // Optional, but non-null if provided

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private Instant expiryDate;

}
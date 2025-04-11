package com.url.shortener.Vyson.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchUrlRequest {
    @Valid
    @NotEmpty(message = "URL list cannot be empty")
    private List<@Valid UrlRequest> urls;
}



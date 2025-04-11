package com.url.shortener.Vyson.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchUrlResponse {
    private List<UrlResponse> results;
    private int successCount;
    private int errorCount;
}



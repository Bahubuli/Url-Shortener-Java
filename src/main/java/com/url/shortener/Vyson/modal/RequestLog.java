package com.url.shortener.Vyson.modal;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter; // Import Lombok annotations
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "request_logs")
@Getter // Lombok: Generates all getter methods
@Setter // Lombok: Generates all setter methods
@NoArgsConstructor // Lombok: Generates a no-argument constructor (required by JPA)
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;
    private String method;
    private String url;
    @Column(length = 512) // User agents can be long
    private String userAgent;
    private String ipAddress;
    public RequestLog(Instant timestamp, String method, String url, String userAgent, String ipAddress) {
        this.timestamp = timestamp;
        this.method = method;
        this.url = url;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        // Note: 'id' is not included here as it's auto-generated
    }
    // No need to write Getters and Setters manually
    // No need to write the no-arg constructor manually
    // No need to write the all-args constructor manually
}
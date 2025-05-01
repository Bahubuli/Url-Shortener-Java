package com.url.shortener.Vyson.repo;

import com.url.shortener.Vyson.modal.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    // Spring Data JPA provides basic CRUD operations automatically
}
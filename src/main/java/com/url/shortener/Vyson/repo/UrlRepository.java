package com.url.shortener.Vyson.repo;

import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UrlRepository extends JpaRepository<UrlData,String> {

    UrlData findByShortUrl(String shortUrl);
    UrlData findByLongUrl(String longUrl);
    int deleteByLongUrl(String longUrl);
    int deleteByShortUrlAndUser(String shortUrl, User user);
    List<UrlData> findByShortUrlAndUser(String longUrl, User user);
    List<UrlData> findByIdAndUser(Long id, User user);
    // Add explicit native query annotation
    @Query(value = "SELECT nextval('url_data_id_seq')", nativeQuery = true)
    Long getNextIdSequence();  // This is now a native SQL query
}

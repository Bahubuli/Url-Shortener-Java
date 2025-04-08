package com.url.shortener.Vyson.repo;

import com.url.shortener.Vyson.modal.UrlData;
import com.url.shortener.Vyson.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UrlRepository extends JpaRepository<UrlData,String> {

    UrlData findByShortUrl(String shortUrl);
    UrlData findByLongUrl(String longUrl);
    int deleteByLongUrl(String longUrl);
    List<UrlData> findByLongUrlAndUser(String longUrl, User user);

}

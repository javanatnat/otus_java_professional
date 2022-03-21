package ru.otus.antibruteforce.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.antibruteforce.model.Blacklist;
import ru.otus.antibruteforce.model.Ipv4Type;

import java.util.List;

public interface BlacklistRepository extends CrudRepository<Blacklist, Long> {
    @Modifying
    @Query("DELETE FROM blacklist b WHERE b.ip = :ip")
    void deleteByIp(@Param("ip") Ipv4Type ip);

    @Override
    @Query("SELECT b.* FROM blacklist b")
    List<Blacklist> findAll();
}

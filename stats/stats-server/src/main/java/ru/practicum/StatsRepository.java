package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Integer> {

    List<EndpointHit> findAllByAppAndUriAndIp(String app, String uri, String ip);
}

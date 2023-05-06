package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.util.List;

@Service
public interface StatsService {

    EndpointHit saveEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique);
}

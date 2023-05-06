package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsRepository;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EntityManager entityManager;
    private final StatsRepository statsRepository;

    @Override
    public EndpointHit saveEndpointHit(EndpointHit endpointHit) {
        endpointHit.setTimestamp(LocalDateTime.now());
        return statsRepository.save(endpointHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViewStats> query = cb.createQuery(ViewStats.class);
        Root<EndpointHit> root = query.from(EndpointHit.class);

        List<Predicate> predicates = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Predicate timestampExpression = cb.between(root.get("timestamp"), LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter));
        predicates.add(timestampExpression);

        if (unique) {
            query.multiselect(root.get("app"), root.get("uri"), cb.countDistinct(root.get("ip")));
        } else {
            query.multiselect(root.get("app"), root.get("uri"), cb.count(root.get("app")));
        }

        if (uris != null) {
            predicates.add(root.get("uri").in(uris));
        }

        query.where(predicates.toArray(new Predicate[]{})).groupBy(root.get("app"), root.get("uri"));

        List<ViewStats> viewStats = entityManager.createQuery(query).getResultList();
        viewStats.sort(Comparator.comparing(ViewStats::getHits).reversed());

        return viewStats;
    }
}

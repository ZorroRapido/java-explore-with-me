package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> saveEndpointHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

    public ResponseEntity<Object> getViewStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        if (start != null) {
            sb.append("start={start}");
            parameters.put("start", start);
        }

        if (end != null) {
            sb.append("end={end}");
            parameters.put("end", end);
        }

        if (uris != null) {
            sb.append("uris={uris}");
            parameters.put("uris", String.join(",", uris));
        }

        if (unique != null) {
            sb.append("unique={unique}");
            parameters.put("unique", unique);
        }

        return get("/stats?" + sb, parameters);
    }
}

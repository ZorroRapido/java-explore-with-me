package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> saveEndpointHit(@RequestBody EndpointHit endpointHit) {
        return ResponseEntity.status(201).body(statsService.saveEndpointHit(endpointHit));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getViewStats(@RequestParam("start") String start,
                                                        @RequestParam("end") String end,
                                                        @RequestParam(name = "uris", required = false) List<String> uris,
                                                        @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return ResponseEntity.ok().body(statsService.getViewStats(start, end, uris, unique));
    }
}

package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    private ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех подборок: pinned={}, from={}, size={}", pinned, from, size);
        return ResponseEntity.ok().body(compilationService.getAllCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    private ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Integer compId) {
        log.info("Получен запрос на получение подборки с id={}", compId);
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId));
    }
}

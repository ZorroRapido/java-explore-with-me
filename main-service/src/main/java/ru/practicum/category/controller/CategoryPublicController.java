package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    private ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех категорий: from={}, size={}", from, size);
        return ResponseEntity.ok().body(categoryService.getAllCategories(from, size));
    }

    @GetMapping("/{catId}")
    private ResponseEntity<CategoryDto> getCategoryById(@PathVariable("catId") Integer catId) {
        log.info("Получен запрос на получение категории с id={}", catId);
        return ResponseEntity.ok().body(categoryService.getCategoryDtoById(catId));
    }
}

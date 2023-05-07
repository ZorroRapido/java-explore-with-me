package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryDtoById(Integer catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(CategoryDto categoryDto, Integer catId);
}

package ru.practicum.category.service;

import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

@Service
public interface CategoryService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryDtoById(Integer catId);

    Category getCategoryById(Integer catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(CategoryDto categoryDto, Integer catId);
}

package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.CategoryAlreadyExistsException;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.ConditionNotMetException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).toList().stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryDtoById(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            log.warn("Category with id={} was not found!", catId);
            throw new CategoryNotFoundException(catId);
        }

        return categoryMapper.toCategoryDto(categoryRepository.getReferenceById(catId));
    }

    @Transactional(readOnly = true)
    @Override
    public Category getCategoryById(Integer catId) {
        return catId != null ? categoryRepository.getReferenceById(catId) : null;
    }

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryMapper.toCategory(newCategoryDto);
            return categoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = String.format("Category with name='%s' already exists!", newCategoryDto.getName());
            log.warn(errorMessage);
            throw new CategoryAlreadyExistsException(e.getMessage(), e.getCause());
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            log.warn("Category with id={} was not found!", catId);
            throw new CategoryNotFoundException(catId);
        }

        if (!eventRepository.countAllByCategoryId(catId).equals(0)) {
            log.warn("Category with related events can not be deleted! (id = {})", catId);
            throw new ConditionNotMetException("Only categories without related events can be deleted.");
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            log.warn("Category with id={} was not found!", catId);
            throw new CategoryNotFoundException(catId);
        }

        Category category = categoryRepository.getReferenceById(catId);
        category.setName(categoryDto.getName());

        try {
            return categoryMapper.toCategoryDto(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            String errorMessage = String.format("Category with name='%s' already exists!", category.getName());
            log.warn(errorMessage);
            throw new ConditionNotMetException(e.getMessage());
        }
    }
}

package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

import static ru.practicum.ewm.utility.EntityFinder.findCategoryOrThrowException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryServicePublicImpl implements CategoryServicePublic {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();
        log.info("Получен список всех категорий");
        return categories;
    }

    @Override
    public Category getCategory(Integer catId) {
        Category category = findCategoryOrThrowException(categoryRepository, catId);
        log.info("Получена категория ID: {}", catId);
        return category;
    }
}
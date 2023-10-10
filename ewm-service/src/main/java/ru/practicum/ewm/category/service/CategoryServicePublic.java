package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryServicePublic {
    List<Category> getCategories(Integer from, Integer size);

    Category getCategory(Integer catId);
}

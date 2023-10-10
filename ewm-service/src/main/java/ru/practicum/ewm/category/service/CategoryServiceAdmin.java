package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.Category;

public interface CategoryServiceAdmin {
    Category addCategory(Category newCategory);

    void deleteCategory(Integer catId);

    Category patchCategory(Integer catId, Category patchedCategory);
}

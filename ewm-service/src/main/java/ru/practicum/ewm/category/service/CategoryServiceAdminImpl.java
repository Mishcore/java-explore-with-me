package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dao.CategoryRepository;
import ru.practicum.ewm.category.model.Category;

import static ru.practicum.ewm.utility.EntityFinder.findCategoryOrThrowException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceAdminImpl implements CategoryServiceAdmin {

    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(Category newCategory) {
        Category category = categoryRepository.save(newCategory);
        log.info("Добавлена новая категория ID: {}", category.getId());
        return category;
    }

    @Override
    public void deleteCategory(Integer catId) {
        findCategoryOrThrowException(categoryRepository, catId);
        categoryRepository.deleteById(catId);
        log.info("Удалена категория ID: {}", catId);
    }

    @Override
    public Category patchCategory(Integer catId, Category patchedCategory) {
        Category category = findCategoryOrThrowException(categoryRepository, catId);
        category.setName(patchedCategory.getName());
        categoryRepository.save(category);
        log.info("Изменена категория ID: {}", catId);
        return category;
    }
}

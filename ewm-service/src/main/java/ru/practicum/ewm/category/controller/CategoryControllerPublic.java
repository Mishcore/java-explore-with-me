package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryServicePublic;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/categories")
public class CategoryControllerPublic {

    private final CategoryServicePublic categoryServicePublic;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Принят запрос на получение списка категорий");
        return categoryServicePublic.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public Category getCategory(@PathVariable @Positive Integer catId) {
        log.info("Принят запрос на получение категории по ID: {}", catId);
        return categoryServicePublic.getCategory(catId);
    }
}

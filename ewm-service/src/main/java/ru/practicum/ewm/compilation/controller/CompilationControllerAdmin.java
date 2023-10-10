package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationServiceAdmin;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationServiceAdmin compilationServiceAdmin;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Принят запрос на добавление новой подборки событий");
        return compilationServiceAdmin.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Integer compId) {
        log.info("Принят запрос на удаление подборки событий ID: {}", compId);
        compilationServiceAdmin.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(
            @PathVariable Integer compId,
            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Принят запрос на обновление подборки событий ID: {}", compId);
        return compilationServiceAdmin.updateCompilation(compId, updateCompilationRequest);
    }
}

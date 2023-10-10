package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dao.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utility.EntityFinder.findCompilationOrThrowException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompilationServicePublicImpl implements CompilationServicePublic {

    private final CompilationRepository compilationRepository;
    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size));
        }
        log.info("Получен список подборок событий");

        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDto(compilation, statsClient))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = findCompilationOrThrowException(compilationRepository, compId);
        log.info("Получена подборка событий ID: {}", compId);
        return CompilationMapper.toCompilationDto(compilation, statsClient);
    }
}

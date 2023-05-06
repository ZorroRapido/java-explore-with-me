package ru.practicum.compilation.service;

import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

import java.util.List;

@Service
public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Integer compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Integer compId);

    CompilationDto updateCompilation(UpdateCompilationRequest request, Integer compId);
}

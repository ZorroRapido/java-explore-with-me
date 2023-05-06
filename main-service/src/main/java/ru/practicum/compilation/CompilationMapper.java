package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(NewCompilationDto compilation);

    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);
}

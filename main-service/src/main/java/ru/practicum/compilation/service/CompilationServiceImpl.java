package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.service.ConsistencyService;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationEvent;
import ru.practicum.compilation.repository.CompilationEventRepository;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EntityManager entityManager;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;
    private final ConsistencyService consistencyService;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> query = cb.createQuery(Compilation.class);
        Root<Compilation> root = query.from(Compilation.class);

        if (pinned != null) {
            query.where(cb.equal(root.get("pinned"), pinned));
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        TypedQuery<Compilation> typedQuery = entityManager.createQuery(query);
        List<CompilationDto> compilations = new PageImpl<>(typedQuery.getResultList(), pageRequest, size).toList().stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        compilations.forEach(compilationDto ->
                compilationDto.setEvents(compilationEventRepository.findByCompilationId(compilationDto.getId()).stream()
                        .map(compilationEvent -> eventRepository.getReferenceById(compilationEvent.getEventId()))
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList())));

        return compilations;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Integer compId) {
        consistencyService.checkCompilationExistence(compId);

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.getReferenceById(compId));
        var events = compilationEventRepository.findByCompilationId(compilationDto.getId()).stream()
                .map(compilationEvent -> eventRepository.getReferenceById(compilationEvent.getEventId()))
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
        compilationDto.setEvents(events);

        return compilationDto;
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto));
        List<Event> events = new ArrayList<>();

        newCompilationDto.getEvents().forEach(eventId -> {
            events.add(eventRepository.getReferenceById(eventId));
            compilationEventRepository.save(new CompilationEvent(compilation.getId(), eventId));
        });

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList()));

        return compilationDto;
    }

    @Transactional
    @Override
    public void deleteCompilationById(Integer compId) {
        consistencyService.checkCompilationExistence(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest request, Integer compId) {
        consistencyService.checkCompilationExistence(compId);

        Compilation compilation = compilationRepository.getReferenceById(compId);

        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }

        if (request.getEvents() != null) {
            List<Integer> eventIds = compilationEventRepository.findByCompilationId(compId).stream()
                    .map(CompilationEvent::getEventId)
                    .collect(Collectors.toList());

            request.getEvents().forEach(eventId -> {
                if (!eventIds.contains(eventId)) {
                    compilationEventRepository.saveAndFlush(new CompilationEvent(compId, eventId));
                }
            });

            eventIds.forEach(eventId -> {
                if (!request.getEvents().contains(eventId)) {
                    compilationEventRepository.deleteByCompilationIdAndEventId(compId, eventId);
                }
            });
        }

        return getCompilationById(compId);
    }
}

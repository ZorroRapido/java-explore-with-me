package ru.practicum.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Service
public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Integer userId);

    ParticipationRequestDto createRequest(Integer userId, Integer eventId);

    ParticipationRequestDto cancelRequest(Integer userId, Integer requestId);
}

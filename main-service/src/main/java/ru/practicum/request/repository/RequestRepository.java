package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {

    List<ParticipationRequest> findAllByRequester(UserDto userDto);

    List<ParticipationRequest> findAllByRequesterAndEvent(UserDto userDto, Event event);

    List<ParticipationRequest> findAllByEvent(Event event);
}

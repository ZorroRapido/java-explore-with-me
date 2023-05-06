package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDto, Integer> {

    Page<UserDto> findByIdIn(List<Integer> ids, Pageable pageable);
}

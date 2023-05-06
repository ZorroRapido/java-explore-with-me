package ru.practicum.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUserById(Integer userId);
}

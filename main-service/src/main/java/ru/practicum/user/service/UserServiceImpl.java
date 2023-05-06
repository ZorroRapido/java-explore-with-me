package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return userRepository.findByIdIn(ids, pageRequest).toList();
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        UserDto userDto = userMapper.toUserDto(newUserRequest);

        try {
            return userRepository.save(userDto);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = String.format("User with email='%s' already exists!", userDto.getEmail());
            log.warn(errorMessage);
            throw new EmailAlreadyExistsException(e.getMessage(), e.getCause());
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Integer userId) {
        checkUserExistence(userId);
        userRepository.deleteById(userId);
    }

    private void checkUserExistence(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} was not found!", userId);
            throw new UserNotFoundException(userId);
        }
    }
}

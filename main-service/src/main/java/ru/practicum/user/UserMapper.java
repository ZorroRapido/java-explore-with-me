package ru.practicum.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toUserDto(NewUserRequest newUserRequest);
}

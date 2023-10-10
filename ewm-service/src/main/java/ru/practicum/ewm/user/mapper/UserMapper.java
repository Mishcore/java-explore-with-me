package ru.practicum.ewm.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest newUserDto) {
        return new User(null, newUserDto.getEmail(), newUserDto.getName());
    }
}

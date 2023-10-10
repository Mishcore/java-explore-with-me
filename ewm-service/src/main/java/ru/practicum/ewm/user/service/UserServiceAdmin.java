package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserServiceAdmin {

    List<UserDto> getUsers(Long[] ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserDto);

    void deleteUser(Long userId);


}

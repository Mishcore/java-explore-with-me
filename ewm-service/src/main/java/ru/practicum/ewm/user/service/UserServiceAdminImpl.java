package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.EmailAlreadyExistsException;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utility.EntityFinder.findUserOrThrowException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceAdminImpl implements UserServiceAdmin {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        List<User> users;
        if (ids != null) {
            users = userRepository.findAllById(Arrays.asList(ids));
        } else {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        log.info("Получен список всех пользователей");
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(NewUserRequest newUserDto) {
        try {
            User user = userRepository.saveAndFlush(UserMapper.toUser(newUserDto));
            log.info("Добавлен новый пользователь ID: {}", user.getId());
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Уже существует пользователь с таким e-mail");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        findUserOrThrowException(userRepository, userId);
        userRepository.deleteById(userId);
        log.info("Удалён пользователь ID: {}", userId);
    }
}

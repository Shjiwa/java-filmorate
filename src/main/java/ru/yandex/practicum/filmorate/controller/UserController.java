package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ExceptionService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ExceptionService exceptionService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя: {}", user.getName());
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение списка пользователей");
        return userService.getUsers();
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (userService.getUserById(user.getId()) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на обновление пользователя с id: {}", user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (userService.getUserById(id) == null || userService.getUserById(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на добавление в друзья пользователей с id: {} и {}", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (userService.getUserById(id) == null || userService.getUserById(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("От пользователя с id: {} поступил запрос на удаление из друзей пользователя с id: {}", id, friendId);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getSharedFriendsList(@PathVariable Long id, @PathVariable Long friendId) {
        if (userService.getUserById(id) == null || userService.getUserById(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение списка общих друзей пользователей с id: {} и {}", id, friendId);
        return userService.getSharedFriendsList(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        if (userService.getUserById(id) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение пользователя с id {}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        if (userService.getUserById(id) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение списка друзей пользователя с id {}", id);
        return userService.getFriends(id);
    }
}
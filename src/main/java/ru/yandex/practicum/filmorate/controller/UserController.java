package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ExceptionService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;
    private final ExceptionService exceptionService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создан пользователь: {} с id {}", user.getName(), user.getId());
        return userStorage.createUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Поступил запрос на получение списка пользователей");
        return userStorage.getUsers();
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (userStorage.getMap().get(user.getId()) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Пользователь с id {} обновлен", user.getId());
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (userStorage.getMap().get(id) == null || userStorage.getMap().get(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("{} и {} теперь друзья",
                userStorage.getUserById(id).getName(), userStorage.getUserById(friendId).getName());
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public  User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (userStorage.getMap().get(id) == null || userStorage.getMap().get(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("{} и {} перестали быть друзьями", userStorage.getUserById(id).getName(),
                userStorage.getUserById(friendId).getName());
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getSharedFriendsList(@PathVariable Long id, @PathVariable Long friendId) {
        if (userStorage.getMap().get(id) == null || userStorage.getMap().get(friendId) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение списка общих друзей пользователей {} и {}",
                userStorage.getUserById(id), userStorage.getUserById(friendId));
        return userService.getSharedFriendsList(id, friendId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        if (userStorage.getMap().get(id) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение пользователя с id {}", id);
        return userStorage.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        if (userStorage.getMap().get(id) == null) {
            exceptionService.throwNotFound();
        }
        log.info("Поступил запрос на получение списка друзей пользователя с id {}", id);
        return userService.getFriends(id);
    }
}
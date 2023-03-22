package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new LinkedHashMap<>();

    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей в текущий момент: " + users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.error("Данный пользователь уже существует.");
            throw new ValidationException("Данный пользователь уже существует.");
        }
        user.setId(users.size() + 1);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {} с id {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Данного пользователя не существует.");
            throw new ValidationException("Данного пользователя не существует.");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {} с id {}", user.getName(), user.getId());
        return user;
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    private int id = 1;

    @GetMapping
    public List<User> getUsers() {
        log.info("Количество пользователей в текущий момент: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (users.containsValue(user)) {
            log.error("Already exist.");
            throw new AlreadyExistException("Already exist.");
        }
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {} с id {}", user.getName(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Not found.");
            throw new NotFoundException("Not found.");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {} с id {}", user.getName(), user.getId());
        return user;
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 1L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {} с id {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        log.info("Пользователи с id: {} и {} теперь друзья", id, friendId);
        return users.get(id);
    }

    @Override
    public User deleteFriend(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
        log.info("Пользователи с id: {} и {} перестали быть друзьями", id, friendId);
        return users.get(id);
    }

    @Override
    public List<User> getSharedFriendsList(Long id, Long friendId) {
        List<User> sharedFriends = new ArrayList<>();
        for (Long userId : getUserById(id).getFriends()) {
            if (getUserById(friendId).getFriends().contains(userId)) {
                sharedFriends.add(getUserById(userId));
            }
        }
        return sharedFriends;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        return getUsers().stream()
                .filter(user -> user.getFriends().contains(id))
                .collect(Collectors.toList());
    }
}
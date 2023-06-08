package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(Long id, Long friendId) {
        userStorage.addFriend(id, friendId);
        return userStorage.getUserById(id);
    }

    public User deleteFriend(Long id, Long friendId) {
        userStorage.deleteFriend(id, friendId);
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getSharedFriendsList(Long id, Long friendId) {
        return userStorage.getSharedFriendsList(id, friendId);
    }
}
package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    List<User> getUsers();

    Map<Long, User> getMap();

    User createUser(User user);

    User updateUser(User user);

    User addFriend(Long id, Long friendId);

    User deleteFriend(Long id, Long friendId);

    List<User> getSharedFriendsList(Long id, Long friendId);

    User getUserById(Long id);

    List<User> getFriends(Long id);
}
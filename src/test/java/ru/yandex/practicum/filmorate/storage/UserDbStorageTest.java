package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.modelFactory.ModelFactory;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private final ModelFactory modelFactory = ModelFactory.getInstance();
    User user;
    User friend;
    User sharedFriend;

    @BeforeEach
    public void beforeEach() {
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FRIENDS");
        user = modelFactory.createUser();

        friend = modelFactory.createUser();
        modelFactory.setNewUserData(friend);

        sharedFriend = modelFactory.createUser();
        modelFactory.setNewUserData(sharedFriend);
    }

    @Test
    public void shouldGetUserById() {

        userDbStorage.createUser(user);
        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void shouldCreateUpdateUser() {
        userDbStorage.createUser(user);
        assertEquals(user, userDbStorage.getUserById(user.getId()));
        assertEquals(user.getName(), userDbStorage.getUserById(user.getId()).getName());

        modelFactory.setNewUserData(user);
        userDbStorage.updateUser(user);
        assertEquals(user, userDbStorage.getUserById(user.getId()));

        assertEquals(1, userDbStorage.getUsers().size());
        assertEquals(user, userDbStorage.getUserById(user.getId()));
    }

    @Test
    public void shouldAddAndDeleteFriend() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());
        assertEquals(1, userDbStorage.getFriends(user.getId()).size());
        assertEquals(0, userDbStorage.getFriends(friend.getId()).size());

        userDbStorage.deleteFriend(user.getId(), friend.getId());
        assertEquals(0, userDbStorage.getFriends(user.getId()).size());
        assertEquals(0, userDbStorage.getFriends(friend.getId()).size());
    }

    @Test
    public void shouldGetSharedFriends() {
        userDbStorage.createUser(user);
        userDbStorage.createUser(friend);
        userDbStorage.createUser(sharedFriend);
        userDbStorage.addFriend(user.getId(), sharedFriend.getId());
        userDbStorage.addFriend(friend.getId(), sharedFriend.getId());
        assertSame(userDbStorage.getSharedFriendsList(user.getId(), friend.getId()).get(0).getId(), sharedFriend.getId());
    }
}

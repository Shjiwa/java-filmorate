package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ExceptionService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final ExceptionService exceptionService;

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        while (rowSet.next()) {
            User user = new User();
            user.setId(rowSet.getLong("USER_ID"));
            user.setName(rowSet.getString("USER_NAME"));
            user.setEmail(rowSet.getString("EMAIL"));
            user.setLogin(rowSet.getString("LOGIN"));
            user.setBirthday(Objects.requireNonNull(rowSet.getDate("BIRTHDAY")).toLocalDate());
            users.add(user);
        }
        return users;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        user.setId(jdbcInsert.executeAndReturnKey(collectToMap(user)).longValue());
        log.debug("Пользователь: {} создан!", user.getName());
        return user;
    }

    private Map<String, Object> collectToMap(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("USER_NAME", user.getName());
        fields.put("LOGIN", user.getLogin());
        fields.put("EMAIL", user.getEmail());
        fields.put("BIRTHDAY", user.getBirthday());
        return fields;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS " +
                     "SET USER_NAME = ?," +
                         "LOGIN = ?," +
                         "EMAIL = ?," +
                         "BIRTHDAY = ? " +
                     "WHERE USER_ID = ?";
        int rowsCount = jdbcTemplate.update(sql,
                user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        if (rowsCount > 0) {
            log.debug("Пользователь: {} обновлен!", user.getName());
            return user;
        }
        log.error("Not found");
        exceptionService.throwNotFound();
        return user;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
        log.debug("Пользователь с id {} и {} теперь друзья!", id, friendId);
        return getUserById(id);
    }

    @Override
    public User deleteFriend(Long id, Long friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, id, friendId);
        log.debug("Пользователь с id {} и {} перестали быть друзьями!", id, friendId);
        return getUserById(id);
    }

    @Override
    public List<User> getSharedFriendsList(Long id, Long friendId) {
        String sql = "SELECT *" +
                     "FROM USERS " +
                     "WHERE USER_ID IN(SELECT FRIEND_ID " +
                                      "FROM FRIENDS " +
                                      "WHERE USER_ID = ?) " +
                                      "AND USER_ID IN(SELECT FRIEND_ID " +
                                                     "FROM FRIENDS " +
                                                     "WHERE USER_ID = ?)";
        return new ArrayList<>(jdbcTemplate.query(sql, this::rowMapToUser, id, friendId));
    }

    @Override
    public User getUserById(Long id) {
        String sql = "SELECT *" +
                     "FROM USERS " +
                     "WHERE USER_ID=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::rowMapToUser, id);
        } catch (DataAccessException e) {
            log.error("Not found");
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT U.*" +
                     "FROM USERS " +
                     "LEFT JOIN FRIENDS F on USERS.USER_ID = F.USER_ID " +
                     "LEFT JOIN USERS U on U.USER_ID = F.FRIEND_ID " +
                     "WHERE USERS.USER_ID = ?";
        List<User> userList = jdbcTemplate.query(sql, this::rowMapToUser, id);
        return userList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private User rowMapToUser(ResultSet rs, int rowNum) throws SQLException {
        java.sql.Date birthdayDate = rs.getDate("BIRTHDAY");
        if (birthdayDate == null) {
            return null;
        }
        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        user.setName(rs.getString("USER_NAME"));
        user.setLogin(rs.getString("LOGIN"));
        user.setEmail(rs.getString("EMAIL"));
        user.setBirthday(birthdayDate.toLocalDate());
        user.setFriends(new HashSet<>());

        return user;
    }
}

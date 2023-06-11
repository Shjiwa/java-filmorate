package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validator.ReplaceNameWithLogin;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Validated
@ReplaceNameWithLogin
public class User {

    private Set<Long> friends = new HashSet<>();

    @Positive
    private Long id;

    @NotBlank(message = "Email is null or blank.")
    @Email(message = "Email is not valid.")
    private String email;

    @Setter(AccessLevel.NONE)
    @NotBlank(message = "Login is null or blank.")
    private String login;

    @Past(message = "Birthday in future.")
    private LocalDate birthday;

    private String name;

    public User(Long id, String email, String login, LocalDate birthday, String name) {
        this.id = id;
        this.email = email;
        setLogin(login);
        this.birthday = birthday;
        this.name = name;
    }

    public void setLogin(String login) {
        if (login == null) {
            this.login = null;
        } else if (login.contains(" ")) {
            this.login = "";
        } else {
            this.login = login;
        }
    }

    public Set<Long> getFriends() {
        return friends;
    }
}

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
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Validated
@ReplaceNameWithLogin
public class User {

    private Set<Long> friends = new HashSet<>();

    @Positive
    private Long id;

    private String name;

    @NotBlank(message = "Email is null or blank.")
    @Email(message = "Email is not valid.")
    private String email;

    @Setter(AccessLevel.NONE)
    @NotBlank(message = "Login is null or blank.")
    private String login;

    @Past(message = "Birthday in future.")
    private LocalDate birthday;


    public void setLogin(String login) {
        if (login == null) {
            this.login = null;
        } else if (login.contains(" ")) {
            this.login = "";
        } else {
            this.login = login;
        }
    }
}

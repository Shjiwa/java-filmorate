package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReplaceNameWithLoginValidator implements ConstraintValidator<ReplaceNameWithLogin, User> {

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}

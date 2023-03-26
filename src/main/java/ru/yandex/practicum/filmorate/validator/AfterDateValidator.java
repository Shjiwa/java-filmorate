package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {

    public String value;

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(AfterDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return false;
        } else {
            return localDate.isAfter(LocalDate.parse(value, formatter));
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Service
public class ExceptionService {
    public void throwNotFound() {
        throw new NotFoundException("Not found");
    }

    public void throwBadRequest(String message) {
        throw new BadRequestException(message);
    }
}

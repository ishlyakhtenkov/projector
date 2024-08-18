package ru.javaprojects.projector.users.error;

import ru.javaprojects.projector.common.error.LocalizedException;

public class TokenException extends LocalizedException {
    public TokenException(String message, String messageCode, Object[] messageArgs) {
        super(message, messageCode, messageArgs);
    }
}
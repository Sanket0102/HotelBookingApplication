package com.example.HotelLakeShore.exception;

import java.sql.SQLException;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

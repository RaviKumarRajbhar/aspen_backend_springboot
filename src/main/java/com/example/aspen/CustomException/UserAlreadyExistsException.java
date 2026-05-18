package com.example.aspen.CustomException;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message)
    {
        super(message);
    }

}

package com.example.aspen.CustomException;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message){
        super(message);
    }
}

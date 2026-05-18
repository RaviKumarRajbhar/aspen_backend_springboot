package com.example.aspen.CustomException;

public class WrongAuthProviderException extends  RuntimeException {
    public WrongAuthProviderException(String message){
        super(message);
    }
}


package com.example.aspen.GlobalExceptionHandler;

import com.example.aspen.CustomException.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex){
        return ResponseEntity.badRequest().body(
                Map.of(
                        "error" , ex.getMessage(),
                        "status" , 400
                )
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public  ResponseEntity<?> handleInvalidCredentialsRequest(InvalidCredentialsException ex){
        Map<String , Object > response = new HashMap<>();
        response.put("success" , false);
        response.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String , Object> response = new HashMap<>();
        response.put("success" , false);
        response.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {

        Map<String , Object> response = new HashMap<>();

        response.put("success" , false);
        response.put("message" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(WrongAuthProviderException.class)
    public ResponseEntity<?> handleWrongAuthProviderException(WrongAuthProviderException ex){

        Map<String , Object> response = new HashMap<>();

        response.put("success" , false);
        response.put("error" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}

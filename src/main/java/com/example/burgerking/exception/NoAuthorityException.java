package com.example.burgerking.exception;

public class NoAuthorityException extends RuntimeException{
    public NoAuthorityException(String massage){
        super(massage);
    }
}

package com.example.burgerking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto<T> {
    private String msg;
    private int statusCode;
    private T result;
    private String username;

    public ResponseDto(String msg, int statuscode) {
        this.msg = msg;
        this.statusCode = statuscode;
    }

    public ResponseDto(String msg, int statuscode, T result) {
        this.msg = msg;
        this.statusCode = statuscode;
        this.result = result;
    }

    public ResponseDto(String msg, int statuscode, String username){
        this.msg = msg;
        this.statusCode = statuscode;
        this.username = username;
    }
}


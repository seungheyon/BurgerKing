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

    public ResponseDto(String msg, int statuscode) {
        this.msg = msg;
        this.statusCode = statuscode;
    }
}


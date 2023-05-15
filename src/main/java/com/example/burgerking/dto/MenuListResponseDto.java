package com.example.burgerking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuListResponseDto<T> {
    private String msg;
    private int statusCode;
    private List<T> menuList;
}


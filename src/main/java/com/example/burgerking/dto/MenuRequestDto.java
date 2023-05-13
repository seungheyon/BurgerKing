package com.example.burgerking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuRequestDto {
    private String menuname;
    private String category;
}

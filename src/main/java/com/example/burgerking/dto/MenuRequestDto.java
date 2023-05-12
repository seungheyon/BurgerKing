package com.example.burgerking.dto;

import com.example.burgerking.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuRequestDto {
    private String menuname;
    private Category category;
}

package com.example.burgerking.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MenuVo {
    private Long menuId;
    private String menuName;
    private String category;
    private String imageUrl;
}



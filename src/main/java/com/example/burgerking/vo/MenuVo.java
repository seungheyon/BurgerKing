package com.example.burgerking.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MenuVo {
    private Long menuid;
    private String menuname;
    private String category;
    private String imageurl;
}



package com.example.burgerking.vo;

import com.example.burgerking.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserVo {
    private String userName;
    private Boolean isAdmin;
}

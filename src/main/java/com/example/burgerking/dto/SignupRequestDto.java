package com.example.burgerking.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.Date;

@Getter
public class SignupRequestDto {

    private String emailid;
    @Pattern(regexp = "(?=.*[a-z])(?=.*[0-9])(?=\\S+$).{4,10}",
            message = "아이디는 알파벳 소문자, 숫자를 입력하고 4~10자리로 구성해주세요.")
    private String username;
    private String phonenumber;
    private String sex;
    private Date birth;
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[~`!@#$%\\\\^&*()-])(?=\\S+$).{8,15}",
            message = "비밀번호는 알파벳 대소문자, 숫자를 입력하고 8~15자리로 구성해주세요.")
    private String  password;
    private boolean admin = false;
    private String admintoken;

}

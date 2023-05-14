package com.example.burgerking.entity;

import com.example.burgerking.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String  password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    //그냥 회원가입
    public User(SignupRequestDto signupRequestDto,String password, UserRoleEnum role) {
        this.email = signupRequestDto.getEmailid();
        this.username = signupRequestDto.getUsername();
        this.password = password;
        this.role = role;
    }
}

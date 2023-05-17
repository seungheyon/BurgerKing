package com.example.burgerking.controller;

import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.exception.PasswordException;
import com.example.burgerking.service.UserService;
import com.example.burgerking.vo.MenuVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

//    @CrossOrigin("*")
//    @PostMapping("/signup")
//    public ResponseEntity signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
//        return userService.signup(signupRequestDto);
//    }

    @PostMapping("/signup")
    public ResponseDto<MenuVo> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        try {
            return userService.signup(signupRequestDto);
        }
        catch (IllegalArgumentException e){
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public ResponseDto<MenuVo> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            return userService.login(loginRequestDto, response);
        }
        catch (IllegalArgumentException e){
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
        catch (PasswordException e){
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/logout")
    public ResponseDto<MenuVo> logout(HttpServletRequest request){
        return userService.logout(request);
    }
}

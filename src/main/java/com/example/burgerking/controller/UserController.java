package com.example.burgerking.controller;

import com.example.burgerking.dto.KakaoUserInfoDto;
import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.exception.PasswordException;
import com.example.burgerking.jwt.JwtUtil;
import com.example.burgerking.security.UserDetailsImpl;
import com.example.burgerking.service.KakaoService;
import com.example.burgerking.service.UserService;
import com.example.burgerking.vo.MenuVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;


    @PostMapping("/signup")
    public ResponseDto<MenuVo> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        try {
            return userService.signup(signupRequestDto);
        } catch (IllegalArgumentException e) {
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("/login")
    public ResponseDto<MenuVo> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            return userService.login(loginRequestDto, response);
        } catch (IllegalArgumentException e) {
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (PasswordException e) {
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/kakao")
    public KakaoUserInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        KakaoUserInfoDto kakaoUserInfoDto = kakaoService.kakaoLogin(code);

        return kakaoUserInfoDto;
    }

    @PostMapping("/logout")
    public ResponseDto<MenuVo> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        try {
            return userService.logout(userDetails.getUser(), request);
        }
        catch (Exception e){
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

}

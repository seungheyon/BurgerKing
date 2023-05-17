package com.example.burgerking.controller;

import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.exception.PasswordException;
import com.example.burgerking.jwt.JwtUtil;
import com.example.burgerking.service.KakaoService;
import com.example.burgerking.service.UserService;
import com.example.burgerking.vo.MenuVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
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
    private final KakaoService kakaoService;

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

    @GetMapping("/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        String createToken = kakaoService.kakaoLogin(code, response);

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/api/menus";
    }
}

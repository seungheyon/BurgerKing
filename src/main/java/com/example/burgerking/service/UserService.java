package com.example.burgerking.service;

import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.entity.User;
import com.example.burgerking.entity.UserRoleEnum;
import com.example.burgerking.jwt.JwtUtil;
import com.example.burgerking.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String signup(SignupRequestDto signupRequestDto) {
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String emailid = signupRequestDto.getEmailid();

        // 회원 중복 확인
        Optional<User> findEmail = userRepository.findByEmail(emailid);
        if (findEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.(email일치)");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(signupRequestDto,password, role);
        userRepository.save(user);
        return "회원가입 성공";
    }

    @Transactional(readOnly = true)
    public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String emailid = loginRequestDto.getEmailid();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmail(emailid).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );


        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername()));

        return "로그인 성공";
    }

}


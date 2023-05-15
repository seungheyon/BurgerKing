package com.example.burgerking.service;

import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.entity.User;
import com.example.burgerking.entity.UserRoleEnum;
import com.example.burgerking.exception.PasswordException;
import com.example.burgerking.jwt.JwtUtil;
import com.example.burgerking.repository.UserRepository;
import com.example.burgerking.vo.MenuVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";


    @Transactional
    public ResponseDto<MenuVo> signup(SignupRequestDto signupRequestDto) {
        String emailId = signupRequestDto.getEmailId();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());   // pw 암호화하여 저장

        // 회원 중복 확인
        Optional<User> findEmail = userRepository.findByEmailId(emailId);
        if (findEmail.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getTokenString().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(signupRequestDto, password, role);
        userRepository.save(user);
        return new ResponseDto<>("회원가입이 완료되었습니다", HttpStatus.OK.value());
    }

    @Transactional(readOnly = true)
    public ResponseDto<MenuVo> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String emailId = loginRequestDto.getEmailId();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmailId(emailId).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );
        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new PasswordException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUserName(), user.getRole()));

        return new ResponseDto<>("로그인이 완료되었습니다", HttpStatus.OK.value());
    }
}


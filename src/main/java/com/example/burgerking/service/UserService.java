package com.example.burgerking.service;

import com.example.burgerking.dto.LoginRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.dto.SignupRequestDto;
import com.example.burgerking.entity.User;
import com.example.burgerking.entity.UserRoleEnum;
import com.example.burgerking.jwt.JwtUtil;
import com.example.burgerking.repository.UserRepository;
import com.example.burgerking.vo.MenuVo;
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
    public ResponseDto<MenuVo> signup(SignupRequestDto signupRequestDto) {
        String password = passwordEncoder.encode(signupRequestDto.getPassword());   // pw 암호화하여 저장
        String emailId = signupRequestDto.getEmailId();

        // 회원 중복 확인
        Optional<User> findEmail = userRepository.findByEmailId(emailId);
        if (findEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.(email일치)");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(signupRequestDto, password, role);
        userRepository.save(user);
        return new ResponseDto<>("회원가입이 완료되었습니다", 200);
    }

    @Transactional(readOnly = true)
    public String login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String emailid = loginRequestDto.getEmailId();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByEmailId(emailid).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );


        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUserName()));

        return "로그인 성공";
    }
}


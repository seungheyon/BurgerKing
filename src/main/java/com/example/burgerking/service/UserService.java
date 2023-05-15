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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @Transactional
//    public ResponseEntity signup(SignupRequestDto signupRequestDto) {
//        String password = passwordEncoder.encode(signupRequestDto.getPassword());
//        String emailid = signupRequestDto.getEmailid();
//
//        // 회원 중복 확인
//        Optional<User> findEmail = userRepository.findByEmail(emailid);
//        if (findEmail.isPresent()) {
//            throw new IllegalArgumentException("중복된 사용자가 존재합니다.(email일치)");
//        }
//
//        UserRoleEnum role = UserRoleEnum.USER;
//
//        User user = new User(signupRequestDto,password, role);
//        userRepository.save(user);
//        return ResponseEntity.status(HttpStatus.OK).body("회원가입 성공");
//    }

    @Transactional
    public ResponseDto<MenuVo> signup(SignupRequestDto signupRequestDto) {
        String password = passwordEncoder.encode(signupRequestDto.getPassword());   // password 암호화하여 저장
        String emailid = signupRequestDto.getEmailid();

        // 회원 중복 확인
        Optional<User> findEmail = userRepository.findByEmail(emailid);
        if (findEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.(email일치)");
        }

        UserRoleEnum role = UserRoleEnum.USER;  // 관리자 권한이 있는건지?

        User user = new User(signupRequestDto,password, role);
        userRepository.save(user);
        //return ResponseEntity.status(HttpStatus.OK).body("회원가입 성공");
        return  new ResponseDto<MenuVo>("회원가입이 완료되었습니다.", HttpStatus.OK.value());
    }

    @Transactional(readOnly = true)
    public ResponseEntity login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
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

        return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
    }

}


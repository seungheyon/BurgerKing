package com.example.burgerking.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.burgerking.dto.SecurityExceptionDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // 토큰이 필요 없는 URL 패턴==============================================
    private static final String[] FILTER_IGNORE_URLS = {"/login","/signup","/redisTest"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);

        // GET 요청, login, signup 요청에 대해서는 필터를 거치지 않음. ==========================
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        if("GET".equalsIgnoreCase(requestMethod)){
            filterChain.doFilter(request,response);
            return;
        }
        if(isIgnoredURL(requestURI)){
            filterChain.doFilter(request,response);
            return;
        }
        //===================================================================================

        if(token != null) {
            if(!jwtUtil.validateToken(token)){
                jwtExceptionHandler(response, "Token Error", HttpStatus.UNAUTHORIZED.value());
                return;
            }
            // redis 에 token 값이 value 로 저장되어 있는지 확인
            System.out.println("tokken : " + token);
            String isLogout = redisTemplate.opsForValue().get(token);
            System.out.println("isLogout : " + isLogout);
            //log.info("isLogout ::: ", isLogout);
//            if(ObjectUtils.isEmpty(isLogout)){  // redis 에 저장되어 있지 않다면, 정상 토큰으로 간주하여 인증 진행
//
//            }
            Claims info = jwtUtil.getUserInfoFromToken(token);
            setAuthentication(info.getSubject());
        }
        // Token Null Error 핸들링 =======================================================
        else{
            jwtExceptionHandler(response, "Token NULL error", HttpStatus.UNAUTHORIZED.value());
        }
        //================================================================================
        filterChain.doFilter(request,response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isIgnoredURL(String requestURI) {
        for (String ignoredUrl : FILTER_IGNORE_URLS) {
            if (requestURI.endsWith(ignoredUrl)) {
                return true;
            }
        }
        return false;
    }
}
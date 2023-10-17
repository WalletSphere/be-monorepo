package com.khomishchak.cryptoportfolio.interceptor;

import com.khomishchak.cryptoportfolio.services.security.JwtService;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public JwtInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = jwtService.getToken(request).orElse("");
        Long userId = jwtService.extractUserId(token);

        request.setAttribute("userId", userId);

        return true;
    }
}

package com.khomishchak.ws.interceptor;

import com.khomishchak.ws.services.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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

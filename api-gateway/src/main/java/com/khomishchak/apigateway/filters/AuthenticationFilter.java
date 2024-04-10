package com.khomishchak.apigateway.filters;

import com.khomishchak.apigateway.exceptions.MissingHeaderException;
import com.khomishchak.apigateway.model.ProcessedTokenResp;
import com.khomishchak.apigateway.service.JwtService;
import com.khomishchak.apigateway.validator.RouterValidator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    private static final String USER_ID_HEADER = "UserId";
    private final RouterValidator routerValidator;
    private final JwtService jwtService;

    public AuthenticationFilter(RouterValidator routerValidator, JwtService jwtService) {
        this.routerValidator = routerValidator;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isPublicEndpoint.test(request)) {
            return handleSecuredRequest(exchange, request, chain);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> handleSecuredRequest(ServerWebExchange exchange, ServerHttpRequest request, GatewayFilterChain chain) {
        if (isAuthMissing(request)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }

        final String token = getAuthHeader(request);
        ProcessedTokenResp resp = jwtService.processToken(token);

        if (!resp.validated()) {
            return this.onError(exchange, HttpStatus.FORBIDDEN);
        }

        updateRequest(exchange, resp.userId());
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).stream()
                .findFirst()
                .orElseThrow(() -> new MissingHeaderException("Authorization header missing"));
    }

    private void updateRequest(ServerWebExchange exchange, Long userId){
        exchange.getRequest().mutate()
                .header(USER_ID_HEADER, String.valueOf(userId))
                .build();
    }
}

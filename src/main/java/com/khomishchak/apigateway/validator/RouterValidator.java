package com.khomishchak.apigateway.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register", "/auth/login", "/users", "/users/authenticate", "/exchangers/used-currencies"
    );

    public Predicate<ServerHttpRequest> isPublicEndpoint =
            request -> openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}

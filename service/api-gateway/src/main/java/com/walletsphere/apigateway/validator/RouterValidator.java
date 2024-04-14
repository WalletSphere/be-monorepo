package com.walletsphere.apigateway.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    // TODO: update logic flow for APIs list
    //  create an API for backoffice where admin(and only admin) will be able to save/update secret keys in db(hashed) for accessing exact APIs
    //  needed for API that can be accessed from both client and other internal service, apis for given change:
    //   /balances/transactions/history
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register", "/auth/login", "/auth/token", "/users", "/users/authenticate", "/exchangers/used-currencies",
            "/balances/history/transactions/amount"
    );

    public Predicate<ServerHttpRequest> isPublicEndpoint =
            request -> openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}

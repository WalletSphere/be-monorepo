package com.khomishchak.apigateway;

import com.khomishchak.apigateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Value("${AUTHENTICATION_SERVICE_URL:http://localhost:8088}")
    private String authenticationServiceUrl;

    @Value("${EXCHANGER_SERVICE_URL:http://localhost:8085}")
    private String exchangerServiceUrl;

    @Value("${FEEDBACK_SERVICE_URL:http://localhost:6273}")
    private String feedbackServiceUrl;

    @Value("${GOAL_SERVICE_URL:http://localhost:8085}")
    private String goalServiceUrl;

    @Value("${USERGOAL_SERVICE_URL:http://localhost:8085}")
    private String userServiceUrl;

    @Value("${PRICING_SERVICE_URL:http://localhost:8082}")
    private String pricingServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authentication-service", r -> r.path("/auth/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .filter(authenticationFilter)
                        )
                        .uri(authenticationServiceUrl))
                .route("exchanger-service", r -> r.path("/exchangers/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .filter(authenticationFilter)
                        )
                        .uri(exchangerServiceUrl))
                .route("feedback-service", r -> r.path("/feedbacks/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .filter(authenticationFilter)
                        )
                        .uri(feedbackServiceUrl))
                .route("goal-service", r -> r.path("/goals/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .filter(authenticationFilter)
                        )
                        .uri(goalServiceUrl))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f
                                .prefixPath("/api/v1")
                                .filter(authenticationFilter)
                        )
                        .uri(userServiceUrl))
                .route("pricing-service", r -> r.path("/crypto-pricing")
                        .uri(pricingServiceUrl))
                .build();
    }
}

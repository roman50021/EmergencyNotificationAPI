package com.fedkoroma.gateway.config;

import com.fedkoroma.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("client-server",
                        r -> r.path("/main/").filters(f -> f.filter(filter)).uri("lb://client-server"))
                .route("security-server",
                        r -> r.path("/auth/").filters(f -> f.filter(filter)).uri("lb://security-server"))
                .build();
    }


}

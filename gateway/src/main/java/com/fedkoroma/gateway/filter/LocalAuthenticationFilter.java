package com.fedkoroma.gateway.filter;

import com.fedkoroma.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//@Profile("local-validation")
//@Component
//public class LocalAuthenticationFilter extends AbstractGatewayFilterFactory<LocalAuthenticationFilter.Config> {
//
//    @Autowired
//    private JwtUtil jwtUtil; // Локальная валидация JWT
//
//    public LocalAuthenticationFilter() {
//        super(Config.class);
//    }
//
//    public static class Config {
//        // Конфигурация фильтра
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                if (jwtUtil.isInvalid(token)) {
//                    return chain.filter(exchange);
//                } else {
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                }
//            }
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        };
//    }
//}

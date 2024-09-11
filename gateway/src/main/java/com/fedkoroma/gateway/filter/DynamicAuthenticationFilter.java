package com.fedkoroma.gateway.filter;

import com.fedkoroma.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DynamicAuthenticationFilter extends AbstractGatewayFilterFactory<DynamicAuthenticationFilter.Config> {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${gateway.token.validation.type}") // local или remote
    private String validationType;

    @Autowired
    private RouteValidator routeValidator;

    public DynamicAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Конфигурация фильтра
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if ("local".equalsIgnoreCase(validationType)) {
                    // Локальная валидация токена
                    if (jwtUtil.isInvalid(token)) {
                        return chain.filter(exchange);
                    }
                } else if ("remote".equalsIgnoreCase(validationType)) {
                    // Удаленная валидация токена через security-server
                    ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8765/auth/validate?token=" + token, String.class);
                    if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("valid")) {
                        return chain.filter(exchange);
                    }
                }

                // Если токен не валиден
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Если токен отсутствует
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
}

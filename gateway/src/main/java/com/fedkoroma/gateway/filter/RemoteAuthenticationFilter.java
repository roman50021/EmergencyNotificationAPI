package com.fedkoroma.gateway.filter;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.context.annotation.Profile;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//@Profile("remote-validation")
//@Component
//public class RemoteAuthenticationFilter extends AbstractGatewayFilterFactory<RemoteAuthenticationFilter.Config> {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    public RemoteAuthenticationFilter() {
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
//                ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8765/auth/validate?token=" + token, String.class);
//                if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("valid")) {
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

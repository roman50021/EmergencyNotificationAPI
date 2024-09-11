package com.fedkoroma.gateway.filter;

//import com.fedkoroma.gateway.util.JwtUtil;
//import io.jsonwebtoken.Claims;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class AuthenticationFilter implements GatewayFilter {
//
//    @Autowired
//    private RouteValidator routerValidator;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//
//        // Пропуск проверки для открытых API
//        if (routerValidator.isSecured.test(request)) {
//            if (this.isAuthMissing(request))
//                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
//
//            final String token = this.getAuthHeader(request).substring(7);
//
//            if (jwtUtil.isInvalid(token)) {
//                System.out.println("Token is invalid: " + token);
//                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
//            }
//
//            // Роль-ориентированная проверка
//            String role = jwtUtil.getRole(token);
//            if (!StringUtils.isBlank(role) && !routerValidator.roleBaseApi.check(request, role))
//                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
//
//            this.populateRequestWithHeaders(exchange, token);
//        }
//        return chain.filter(exchange);
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(httpStatus);
//        return response.setComplete();
//    }
//
//    private String getAuthHeader(ServerHttpRequest request) {
//        return request.getHeaders().getOrEmpty("Authorization").get(0);
//    }
//
//    private boolean isAuthMissing(ServerHttpRequest request) {
//        return !request.getHeaders().containsKey("Authorization");
//    }
//
//    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
//        Claims claims = jwtUtil.getAllClaimsFromToken(token);
//        exchange.getRequest().mutate()
//                .header("id", String.valueOf(claims.get("id")))
//                .header("role", String.valueOf(claims.get("role")))
//                .build();
//    }
//}

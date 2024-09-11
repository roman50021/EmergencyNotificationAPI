package com.fedkoroma.gateway.filter;

import com.fedkoroma.gateway.enums.Roles;
import com.fedkoroma.gateway.enums.Urls;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/token",
            "/auth/validate",
            "/auth/confirm",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));


    public static final Map<String, List<String>> roleBaseApiEndpoints =
            Map.of(
                    Roles.ADMIN.label,
                    List.of(
                            Urls.GET_DATA.label
                    ),
                    Roles.USER.label,
                    List.of(
                            Urls.GET_DATA.label
                    ));

    public MapCheck<ServerHttpRequest, String> roleBaseApi = (request, role) -> {
        if (roleBaseApiEndpoints.containsKey(role))
            return !roleBaseApiEndpoints.get(role).stream().noneMatch(uri -> request.getURI().getPath().contains(uri));

        return false;
    };

    @FunctionalInterface
    public interface MapCheck<T, U> {
        public Boolean check(T t, U u);
    }

}

package gateway_microservice.gateway_microservice.filter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints= List.of(
            "/auth/register",
            "/auth/login"
    );

    public Predicate<org.springframework.http.server.reactive.ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
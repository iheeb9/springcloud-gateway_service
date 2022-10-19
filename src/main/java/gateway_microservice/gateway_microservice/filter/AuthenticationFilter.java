package gateway_microservice.gateway_microservice.filter;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;

import gateway_microservice.gateway_microservice.token.jwtUtil;
import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter{

    @Autowired
    private RouterValidator routerValidator;//custom route validator
    @Autowired
    private jwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
       org.springframework.http.server.reactive.ServerHttpRequest request =exchange.getRequest();
       if (routerValidator.isSecured.apply(request)) {
          if (this.isAuthMissing(request))
         return this.onError(exchange, "Authorization header is invalid");
           
         final String token = this.getAuthHeader(request);
       //System.out.println(token);
         if (jwtUtil.isInvalid(token))
           return this.onError(exchange, "Authorization header is invalid");
       
      this.populateRequestWithHeaders(exchange, token);
       }
        return chain.filter(exchange);
    }


    /*PRIVATE*/

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
    	  org.springframework.http.server.reactive.ServerHttpResponse response =exchange.getResponse();
        response.setRawStatusCode(HttpStatus.SC_UNAUTHORIZED);
        return response.setComplete();
    }

    private String getAuthHeader(org.springframework.http.server.reactive.ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(org.springframework.http.server.reactive.ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("id", String.valueOf(claims.get("id")))
                .header("role", String.valueOf(claims.get("role")))
                .build();
    }
}

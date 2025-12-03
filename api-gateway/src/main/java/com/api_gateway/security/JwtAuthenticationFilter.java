package com.api_gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);

            String username = claims.getSubject();
            String role = (String) claims.get("role");

            log.info("User: {} | Role: {}", username, role);

            // ----------------------------------------------------------------
            // CRITICAL FIX for Spring Cloud Gateway 4.x:
            // Create a NEW HttpHeaders instance (MUTABLE)
            // ----------------------------------------------------------------
            HttpHeaders mutableHeaders = new HttpHeaders();
            mutableHeaders.addAll(exchange.getRequest().getHeaders()); // copy original
            mutableHeaders.add("X-Auth-Username", username);
            mutableHeaders.add("X-Auth-Role", role != null ? role : "");

            // Rebuild NEW request (no mutation on original request)
            ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    return mutableHeaders; // return fully mutable headers
                }
            };
            // ----------------------------------------------------------------

            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception ex) {
            log.error("JWT validation failed", ex);
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}


package co.com.crediya.model.security;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface TokenProvider {

    Mono<String> generateToken(String username, Map<String, Object> claims);
    Mono<Boolean> validateToken(String token);
}

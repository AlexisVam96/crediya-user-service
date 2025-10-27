import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.security.JwtAuthenticationManager;
import co.com.crediya.security.JwtSecurityContextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtSecurityContextRepositoryTest {

    private JwtAuthenticationManager jwtAuthenticationManager;
    private JwtSecurityContextRepository repository;

    @BeforeEach
    void setUp() {
        jwtAuthenticationManager = mock(JwtAuthenticationManager.class);
        repository = new JwtSecurityContextRepository(jwtAuthenticationManager);
    }

    @Test
    void load_withValidBearerToken_shouldReturnSecurityContext() {
        String token = "valid.jwt.token";
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        SecurityContext context = new SecurityContextImpl(auth);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(mock(org.springframework.http.server.reactive.ServerHttpRequest.class));
        when(exchange.getRequest().getHeaders()).thenReturn(new HttpHeaders() {{
            set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }});
        when(jwtAuthenticationManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(auth));

        Mono<SecurityContext> result = repository.load(exchange);

        StepVerifier.create(result)
                .expectNextMatches(sc -> sc.getAuthentication().getCredentials().equals(token))
                .verifyComplete();
    }

    @Test
    void load_withMissingOrInvalidHeader_shouldEmitUserCustomException() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(mock(org.springframework.http.server.reactive.ServerHttpRequest.class));
        when(exchange.getRequest().getHeaders()).thenReturn(new HttpHeaders());

        Mono<SecurityContext> result = repository.load(exchange);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof UserCustomException);
                    UserCustomException ex = (UserCustomException) throwable;
                    assertEquals("Missing or invalid Authorization header", ex.getMessage());
                    assertEquals(ErrorType.AUTH, ex.getType());
                })
                .verify();
    }
}
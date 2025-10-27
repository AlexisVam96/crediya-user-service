import co.com.crediya.model.exception.UserCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtAuthenticationFilterTest {

    /*
    private JwtAuthenticationFilter filter;
    private ReactiveAuthenticationManager authenticationManager;
    private ServerWebExchange exchange;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() throws Exception {
        authenticationManager = mock(ReactiveAuthenticationManager.class);
        filter = new JwtAuthenticationFilter();
        // Inject mock authenticationManager using reflection
        Field field = JwtAuthenticationFilter.class.getDeclaredField("authenticationManager");
        field.setAccessible(true);
        field.set(filter, authenticationManager);
        exchange = mock(ServerWebExchange.class);
        chain = mock(WebFilterChain.class);
    }
    @Test
    void filter_shouldAuthenticateAndContinueChain_onValidToken() {
        String token = "valid.jwt.token";
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        Authentication auth = new UsernamePasswordAuthenticationToken("user", token);
        when(authenticationManager.authenticate(any())).thenReturn(Mono.just(auth));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals(token, captor.getValue().getCredentials());
    }

    @Test
    void filter_shouldReturnError_onMissingToken() {
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders(); // No Authorization header

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof UserCustomException);
                    assertEquals("Missing or invalid Token", error.getMessage());
                })
                .verify();
    }

    @Test
    void filter_shouldReturnError_onInvalidToken() {
        String token = "Bearer invalid.token";
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(authenticationManager.authenticate(any())).thenReturn(Mono.error(new UserCustomException("Invalid token", null)));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof UserCustomException);
                    assertEquals("Invalid token", error.getMessage());
                })
                .verify();
    }

     */
}
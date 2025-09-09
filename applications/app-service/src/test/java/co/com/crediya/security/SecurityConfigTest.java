import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.security.JwtAuthenticationFilter;
import co.com.crediya.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = mock(JwtAuthenticationFilter.class);
        securityConfig = new SecurityConfig(jwtAuthenticationFilter);
    }

    @Test
    void filterChain_shouldReturnSecurityWebFilterChain() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        assertDoesNotThrow(() -> {
            SecurityWebFilterChain chain = securityConfig.filterChain(http);
            assertNotNull(chain);
        });
    }

    @Test
    void customAccessDeniedHandler_shouldReturnUserCustomException() {
        ServerAccessDeniedHandler handler = securityConfig.customAccessDeniedHandler();
        ServerWebExchange exchange = mock(ServerWebExchange.class);

        StepVerifier.create(handler.handle(exchange, new AccessDeniedException("denied")))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof UserCustomException);
                    assertTrue(error.getMessage().contains("Access Denied"));
                })
                .verify();
    }
}
import co.com.crediya.security.JwtTokenProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class JwtTokenProviderAdapterTest {

    private JwtTokenProviderAdapter provider;
    private final String secret = "mySuperSecretKeyForJwtAuthentication1234567890";
    private final int expiration = 60000;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProviderAdapter();
        Field secretField = JwtTokenProviderAdapter.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(provider, secret);

        Field expirationField = JwtTokenProviderAdapter.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(provider, expiration);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        StepVerifier.create(provider.generateToken("testuser", claims))
                .expectNextMatches(token -> token != null && token.split("\\.").length == 3)
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = provider.generateToken("testuser", claims).block();
        StepVerifier.create(provider.validateToken(token))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";
        StepVerifier.create(provider.validateToken(invalidToken))
                .expectNext(false)
                .verifyComplete();
    }
}
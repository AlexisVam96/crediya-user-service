import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.security.JwtAuthenticationManager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

class JwtAuthenticationManagerTest {

    private JwtAuthenticationManager authenticationManager;
    private String secret = "mySuperSecretKeyForJwtAuthentication1234567890";
    private String encodedSecret;

    @BeforeEach
    void setUp() throws Exception {
        authenticationManager = new JwtAuthenticationManager();
        Field secretKeyField = JwtAuthenticationManager.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(authenticationManager, secret);
        encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    private String generateToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secret.getBytes()));
        return Jwts.builder()
                .setSubject(username)
                .claim("sub", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void authenticate_shouldReturnAuthenticationOnValidToken() {
        String token = generateToken("testuser", "ADMIN");
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", token);

        Mono<Authentication> result = authenticationManager.authenticate(auth);

        StepVerifier.create(result)
                .expectNextMatches(a -> a.getPrincipal().equals("testuser")
                        && a.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")))
                .verifyComplete();
    }

    @Test
    void authenticate_shouldReturnErrorOnInvalidToken() {
        String invalidToken = "invalid.token.value";
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", invalidToken);

        Mono<Authentication> result = authenticationManager.authenticate(auth);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof UserCustomException
                        && e.getMessage().contains("Error process token"))
                .verify();
    }
}
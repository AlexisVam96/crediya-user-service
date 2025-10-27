import co.com.crediya.security.BCryptPasswordEncoderAdapter;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BCryptPasswordEncoderAdapterTest {

    private final BCryptPasswordEncoderAdapter adapter = new BCryptPasswordEncoderAdapter();

    @Test
    void encode_shouldReturnEncodedPassword() {
        String rawPassword = "myPassword";
        StepVerifier.create(adapter.encode(rawPassword))
                .expectNextMatches(encoded -> !encoded.equals(rawPassword) && encoded.length() > 0)
                .verifyComplete();
    }

    @Test
    void matches_shouldReturnTrueForMatchingPasswords() {
        String rawPassword = "myPassword";
        String encoded = adapter.encode(rawPassword).block();
        StepVerifier.create(adapter.matches(rawPassword, encoded))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void matches_shouldReturnFalseForNonMatchingPasswords() {
        String rawPassword = "myPassword";
        String encoded = adapter.encode("otherPassword").block();
        StepVerifier.create(adapter.matches(rawPassword, encoded))
                .expectNext(false)
                .verifyComplete();
    }
}
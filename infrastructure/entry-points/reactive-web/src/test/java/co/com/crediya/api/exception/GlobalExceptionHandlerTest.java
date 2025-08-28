package co.com.crediya.api.exception;


import co.com.crediya.api.config.CorsConfig;
import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.UserCustomException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@WebFluxTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
@Import({GlobalExceptionHandler.class, GlobalErrorAttributes.class, CorsConfig.class})
@ContextConfiguration(classes = {GlobalExceptionHandler.class, GlobalExceptionHandlerTest.DummyController.class})
class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @TestConfiguration
    static class WebPropertiesConfig {
        @Bean
        public WebProperties webProperties() {
            return new WebProperties();
        }
    }

    @RestController
    static class DummyController {
        @GetMapping("/error")
        public void error() throws UserCustomException {
            throw new UserCustomException("Test exception", ErrorType.VALIDATION);
        }
    }

    @Test
    void handleUserCustomException_shouldReturnCustomError() {
        webTestClient.get()
                .uri("/error")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Bad Request")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Test exception")
                .jsonPath("$.type").isEqualTo("VALIDATION");
    }
}
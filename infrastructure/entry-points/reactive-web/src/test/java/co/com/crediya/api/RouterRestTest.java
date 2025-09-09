package co.com.crediya.api;

import ch.qos.logback.core.subst.Token;
import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.LoginDto;
import co.com.crediya.api.dto.TokenDto;
import co.com.crediya.api.dto.UserDto;
import co.com.crediya.api.mapper.LoginDtoMapper;
import co.com.crediya.api.mapper.TokenDtoMapper;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.model.security.AuthToken;
import co.com.crediya.model.security.Login;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserDtoMapper userDtoMapper;

    @MockBean
    private LoginDtoMapper loginDtoMapper;

    @MockBean
    private TokenDtoMapper tokenDtoMapper;

    private UserDto userDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        // Set other required fields as needed
        return userDto;
    }

    private User user() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        // Set other required fields as needed
        return user;
    }

    private CreateUserDto createUserDto() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createUserDto.setEmail("john.doe@example.com");
        // Set other required fields as needed
        return createUserDto;
    }


    @Test
    void testListenGETAllUsers() {
        // Arrange
        User user = user();
        UserDto userDto = userDto();

        when(userUseCase.getAllUsers()).thenReturn(Flux.just(user));
        when(userDtoMapper.toResponse(List.of(user))).thenReturn(Collections.singletonList(userDto));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(userList -> {
                    Assertions.assertThat(userList).isNotEmpty();
                    Assertions.assertThat(userList)
                            .extracting(UserDto::getEmail)
                            .contains("john.doe@example.com");
                });
    }

    @Test
    void testListenPOSTSaveUser_shouldReturnOkResponse() {
        CreateUserDto createUserDto = createUserDto();
        User user = user();
        UserDto userDtoResponse = userDto();

        when(userDtoMapper.toModel(any(CreateUserDto.class))).thenReturn(user);
        when(userUseCase.save(any(User.class))).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(any(User.class))).thenReturn(userDtoResponse);

        webTestClient.post()
                .uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(response -> {
                    Assertions.assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    void testListenGETFindUserByDocumentNumber_shouldReturnOkResponse() {
        String documentNumber = "12345678";
        User user = user();
        UserDto userDtoResponse = userDto();

        when(userUseCase.getUserByDocumentNumber(documentNumber)).thenReturn(Mono.just(user));
        when(userDtoMapper.toResponse(user)).thenReturn(userDtoResponse);

        webTestClient.get()
                .uri("/api/v1/user/{documentNumber}", documentNumber)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(response -> {
                    Assertions.assertThat(response.getEmail()).isEqualTo("john.doe@example.com");
                });
    }

    @Test
    void testListenPOSTloginUser_shouldReturnOkResponse() {
        // Arrange
        String email = "john.doe@example.com";
        String password = "password123";

        var loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        // Mocked domain model and token objects
        var loginModel = new Login();
        loginModel.setEmail(email);
        loginModel.setPassword(password);

        var tokenModel = new AuthToken();
        tokenModel.setToken("mocked-jwt-token");

        var tokenResponse = new TokenDto();
        tokenResponse.setToken("mocked-jwt-token");

        // Mock mappers and use case
        when(loginDtoMapper.toModel(any(LoginDto.class))).thenReturn(loginModel);
        when(userUseCase.login(any())).thenReturn(Mono.just(tokenModel));
        when(tokenDtoMapper.toResponse(any(AuthToken.class))).thenReturn(tokenResponse);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .value(response -> {
                    Assertions.assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
                });
    }

}

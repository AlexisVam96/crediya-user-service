package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.UserDto;
import co.com.crediya.api.mapper.UserDtoMapper;
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



}

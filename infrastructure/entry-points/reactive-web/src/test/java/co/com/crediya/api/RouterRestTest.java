package co.com.crediya.api;

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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    void testListenGETAllUsersSuccessfully2() {
        UserDto userDto = userDto();
        User user = user();

        when(userUseCase.getAllUsers()).thenReturn(Flux.just(user));
        when(userDtoMapper.toResponse(List.of(user))).thenReturn(Collections.singletonList(userDto));


        Flux<UserDto> responseBody = webTestClient.get()
                .uri("/api/v1/user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete();
    }



}

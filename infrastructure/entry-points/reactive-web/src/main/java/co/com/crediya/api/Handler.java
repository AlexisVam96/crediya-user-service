package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserUseCase userUseCase;

    private final UserDtoMapper userDtoMapper;

    public Mono<ServerResponse> listenGETAllUsers(ServerRequest serverRequest) {
        return userUseCase.getAllUsers()
                .collectList()
                .map(userDtoMapper::toResponse) // Maps List<User> to List<UserDto>
                .flatMap(userDtoList -> ServerResponse.ok().bodyValue(userDtoList));
    }

    public Mono<ServerResponse> listenPOSTSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserDto.class)
                .map(userDtoMapper::toModel)
                .flatMap(userUseCase::save)
                .map(userDtoMapper::toResponse)
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto));
    }

}

package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.test.util.AssertionErrors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserReactorRepositoryAdapterTest {

    private UserReactorRepository repository;
    private ObjectMapper mapper;
    private UserReactorRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(UserReactorRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new UserReactorRepositoryAdapter(repository, mapper);
    }

    @Test
    void existsByEmail_shouldDelegateToRepository() {
        String email = "test@example.com";
        when(repository.existsByEmail(email)).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existsByEmail(email);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository, times(1)).existsByEmail(email);
    }

    @Test
    void constructor_shouldCreateAdapter() {
        // Given
        RoleReactorRepository repository = mock(RoleReactorRepository.class);
        ObjectMapper mapper = mock(ObjectMapper.class);

        // When
        RoleReactorRepositoryAdapter adapter = new RoleReactorRepositoryAdapter(repository, mapper);

        // Then
        Assertions.assertNotNull(adapter);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = new User(); // Set fields as needed
        User user2 = new User();
        UserReactorRepositoryAdapter spyAdapter = spy(adapter);

        doReturn(Flux.just(user1, user2)).when(spyAdapter).findAll();

        Flux<User> result = spyAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    void findByDocumentNumber_shouldReturnUser() {
        String documentNumber = "12345678";
        UserEntity userEntity = new UserEntity();
        userEntity.setDocumentNumber(documentNumber);
        User user = new User();
        user.setDocumentNumber(documentNumber);

        when(repository.findByDocumentNumber(documentNumber)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = adapter.findByDocumentNumber(documentNumber);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getDocumentNumber().equals(documentNumber))
                .verifyComplete();

        verify(repository, times(1)).findByDocumentNumber(documentNumber);
        verify(mapper, times(1)).map(userEntity, User.class);
    }

    @Test
    void findAll_shouldReturnAllUsers2() {
        UserEntity entity1 = new UserEntity();
        UserEntity entity2 = new UserEntity();
        User user1 = new User();
        User user2 = new User();

        when(repository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, User.class)).thenReturn(user1);
        when(mapper.map(entity2, User.class)).thenReturn(user2);

        Flux<User> result = adapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getDocumentNumber() == null)
                .expectNextMatches(u -> u.getDocumentNumber() == null)
                .verifyComplete();

    }

}
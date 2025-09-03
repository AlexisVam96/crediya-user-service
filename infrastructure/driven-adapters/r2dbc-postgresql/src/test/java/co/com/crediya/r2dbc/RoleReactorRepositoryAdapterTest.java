package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RoleReactorRepositoryAdapterTest {

    private RoleReactorRepository repository;
    private ObjectMapper mapper;
    private RoleReactorRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(RoleReactorRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new RoleReactorRepositoryAdapter(repository, mapper);
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
    void existsByIdRole_shouldDelegateToRepository() {
        Integer idRole = 1;
        when(repository.existsByIdRole(idRole)).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existsByIdRole(idRole);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository, times(1)).existsByIdRole(idRole);
    }

}
package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class UserReactorRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Integer, UserReactorRepository>
 implements co.com.crediya.model.user.gateways.UserRepository
{
    public UserReactorRepositoryAdapter(UserReactorRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.info("Verifying if user exists with email: {}", email);
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        return repository.findByDocumentNumber(documentNumber)
                .map(entity -> {
                    log.info("User found with document number: {}", documentNumber);
                    return mapper.map(entity, User.class);
                });
    }

    public Flux<User> findAll() {
        log.info("Fetching all users from the database");
        return super.findAll();
    }

}

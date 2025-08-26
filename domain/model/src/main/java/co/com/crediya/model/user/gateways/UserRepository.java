package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    public Flux<User> findAll();
    public Mono<User> findById(Integer id);
    public Mono<User> save(User user);

    public Mono<Boolean> existsByEmail(String email);
}

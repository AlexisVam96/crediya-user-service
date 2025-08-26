package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    public Flux<Role> findAll();
    public Mono<Role> findById(Integer id);
    public Mono<Role> save(Role role);

}

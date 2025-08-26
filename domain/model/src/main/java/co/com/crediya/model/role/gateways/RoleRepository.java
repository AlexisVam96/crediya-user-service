package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    public Mono<Role> findById(Integer id);
}

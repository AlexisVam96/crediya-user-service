package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    public Mono<Boolean> existsByIdRole(Integer idRole);

    public Mono<Role> findByIdRole(Integer idRole);
}

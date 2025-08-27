package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.r2dbc.entity.RoleEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class RoleReactorRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, Integer, RoleReactorRepository>
        implements RoleRepository
{
    public RoleReactorRepositoryAdapter(RoleReactorRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }


    @Override
    public Mono<Boolean> existsByIdRole(Integer idRole) {
        log.info("Checking existence of Role with idRole: {}", idRole);
        return repository.existsByIdRole(idRole);
    }
}

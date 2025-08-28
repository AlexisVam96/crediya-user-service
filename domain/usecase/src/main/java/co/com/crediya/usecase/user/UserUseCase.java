package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.crediya.usecase.transaction.TransactionManager;

import java.util.logging.Logger;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Logger log = Logger.getLogger(UserUseCase.class.getName());

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TransactionManager transactionManager;

    public Flux<User> getAllUsers() {
        log.info("UserUseCase.getAllUsers: Starting getAllUsers for user");
        return transactionManager.doInTransaction(userRepository.findAll());
    }

    public Mono<User> save(User user) {
        log.info("UserUseCase.save: Starting save for user " + user);

        if (!hasText(user.getFirstName()) || !hasText(user.getLastName()) ||
                !hasText(user.getEmail()) || user.getSalary() == null) {
            log.warning("Required fields missing for user: " + user);
            return Mono.error(new UserCustomException("Required fields must not be null or empty", "USR-001"));
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            log.warning("Invalid email format: " + user.getEmail());
            return Mono.error(new UserCustomException("Invalid email format", "USR-002"));
        }

        if (user.getSalary().doubleValue() < 0 || user.getSalary().doubleValue() > 15000000) {
            log.warning("Salary out of range for user: " + user.getSalary());
            return Mono.error(new UserCustomException("Salary must be between 0 and 15,000,000", "USR-003"));
        }

        return transactionManager.doInTransaction(
                userRepository.existsByEmail(user.getEmail())
                        .flatMap(emailExists -> {
                            if (emailExists) {
                                log.warning("Email already registered: " + user.getEmail());
                                return Mono.error(new UserCustomException("Email already registered", "USR-004"));
                            }
                            return roleRepository.existsByIdRole(user.getIdRole());
                        })
                        .flatMap(roleExists -> {
                            if (!roleExists) {
                                log.warning("idRole does not exist: " + user.getIdRole());
                                return Mono.error(new UserCustomException("idRole does not exist", "USR-005"));
                            }
                            log.info("Saving user: " + user);
                            return userRepository.save(user);
                        })
        );
    }

    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

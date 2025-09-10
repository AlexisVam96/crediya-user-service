package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.ErrorType;
import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.security.AuthToken;
import co.com.crediya.model.security.Login;
import co.com.crediya.model.security.TokenProvider;
import co.com.crediya.model.security.PasswordEncoder;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import co.com.crediya.usecase.transaction.TransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Logger log = Logger.getLogger(UserUseCase.class.getName());

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TransactionManager transactionManager;

    private final TokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    public Flux<User> getAllUsers() {
        log.info("UserUseCase.getAllUsers: Starting getAllUsers for user");
        return transactionManager.doInTransaction(userRepository.findAll());
    }

    public Mono<User> getUserByDocumentNumber(String documentNumber) {
        log.info("UserUseCase.getUserByDocumentNumber: Starting getUserByDocumentNumber for documentNumber " + documentNumber);
        return transactionManager.doInTransaction(userRepository.findByDocumentNumber(documentNumber))
                .switchIfEmpty(Mono.error(new UserCustomException("User's document number not found", ErrorType.NOT_FOUND)));
    }

    public Mono<AuthToken> login(Login login) {
        log.info("UserUseCase.login: Starting login for email " + login.getEmail());
        return userRepository.findByEmail(login.getEmail())
            .switchIfEmpty(Mono.error(new UserCustomException("User's email not found", ErrorType.NOT_FOUND)))
            .flatMap(userDb -> passwordEncoder.matches(login.getPassword(), userDb.getPassword())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new UserCustomException("Invalid credentials", ErrorType.VALIDATION));
                    }
                    return roleRepository.findByIdRole(userDb.getIdRole())
                        .switchIfEmpty(Mono.error(new UserCustomException("Role not found", ErrorType.NOT_FOUND)))
                        .flatMap(role -> {
                            Map<String, Object> claims = new HashMap<>();
                            claims.put("role", role.getName());
                            claims.put("email", userDb.getEmail());
                            return tokenProvider.generateToken(userDb.getEmail(), claims)
                                    .map(AuthToken::new);
                        });
                })
            );
    }

    public Mono<User> save(User user) {
        log.info("UserUseCase.save: Starting save for user " + user);

        if (!hasText(user.getFirstName()) || !hasText(user.getLastName()) ||
                !hasText(user.getEmail()) || user.getSalary() == null) {
            log.warning("Required fields missing for user: " + user);
            return Mono.error(new UserCustomException("Required fields must not be null or empty", ErrorType.VALIDATION));
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            log.warning("Invalid email format: " + user.getEmail());
            return Mono.error(new UserCustomException("Invalid email format", ErrorType.VALIDATION));
        }

        if (user.getSalary().doubleValue() < 0 || user.getSalary().doubleValue() > 15000000) {
            log.warning("Salary out of range for user: " + user.getSalary());
            return Mono.error(new UserCustomException("Salary must be between 0 and 15,000,000", ErrorType.VALIDATION));
        }

        return transactionManager.doInTransaction(
                userRepository.existsByEmail(user.getEmail())
                        .flatMap(emailExists -> {
                            if (emailExists) {
                                log.warning("Email already registered: " + user.getEmail());
                                return Mono.error(new UserCustomException("Email already registered", ErrorType.VALIDATION));
                            }
                            return roleRepository.existsByIdRole(user.getIdRole());
                        })
                        .flatMap(roleExists -> {
                            if (!roleExists) {
                                log.warning("idRole does not exist: " + user.getIdRole());
                                return Mono.error(new UserCustomException("idRole does not exist", ErrorType.VALIDATION));
                            }
                            // Encode password before saving
                            return passwordEncoder.encode(user.getPassword())
                                    .map(encodedPassword -> {
                                        user.setPassword(encodedPassword);
                                        return user;
                                    });
                        })
                        .flatMap(userToSave -> {
                            log.info("Saving user: " + userToSave);
                            return userRepository.save(userToSave);
                        })
        );
    }

    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

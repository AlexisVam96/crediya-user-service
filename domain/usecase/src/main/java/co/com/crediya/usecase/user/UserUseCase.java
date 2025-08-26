package co.com.crediya.usecase.user;

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
    private final TransactionManager transactionManager;

    public Flux<User> getAllUsers() {
        log.info("UserUseCase.getAllUsers: Starting getAllUsers for user");
        return transactionManager.doInTransaction(userRepository.findAll());
    }

    public Mono<User> findUserById(Integer id) {
        log.info("UserUseCase.findUserById: Starting findUserById for user");
        return transactionManager.doInTransaction(userRepository.findById(id));
    }

    public Mono<User> save(User user) {
        log.info("UserUseCase.save: Starting save for user");
        return transactionManager.doInTransaction(Mono.just(user)
                .doOnNext(u -> log.info("Validating user: "+ u))
                .filter(u -> hasText(u.getFirstName()) && hasText(u.getLastName()) && hasText(u.getEmail()) && u.getSalary() != null)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warning("Required fields missing for user: " + user);
                    return Mono.error(new IllegalArgumentException("Required fields must not be null or empty"));
                }))
                .filter(u -> EMAIL_PATTERN.matcher(u.getEmail()).matches())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warning("Invalid email format: " + user.getEmail());
                    return Mono.error(new IllegalArgumentException("Invalid email format"));
                }))
                .filter(u -> u.getSalary().doubleValue() >= 0 && u.getSalary().doubleValue() <= 15000000)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warning("Salary out of range for user: " + user.getSalary());
                    return Mono.error(new IllegalArgumentException("Salary must be between 0 and 15,000,000"));
                }))
                .flatMap(u -> {
                    log.info("Checking if email already exists: " + u.getEmail());
                    return userRepository.existsByEmail(u.getEmail())
                            .flatMap(exists -> {
                                if (exists) {
                                    log.warning("Email already registered: " + u.getEmail());
                                    return Mono.error(new IllegalArgumentException("Email already registered"));
                                } else {
                                    log.info("Saving user: " + u);
                                    return userRepository.save(u);
                                }
                            });
                }));
    }

    private static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

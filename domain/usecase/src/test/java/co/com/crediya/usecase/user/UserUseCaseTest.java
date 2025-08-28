package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.transaction.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {

    @InjectMocks
    private UserUseCase userUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private RoleRepository roleRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1996,11,25));
        user.setPhoneNumber("1234567890");
        user.setEmail("john.doe@example.com");
        user.setSalary(new BigDecimal(10000));
        user.setIdRole(1);
    }

    @Test
    void mustfindAllUsersSuccessfully() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));
        when(transactionManager.doInTransaction(any(Flux.class))).thenReturn(Flux.just(user));

        StepVerifier.create(userUseCase.getAllUsers())
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustSaveUserSuccessfully() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(roleRepository.existsByIdRole(user.getIdRole())).thenReturn(Mono.just(true));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.save(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustFailToSaveWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Email already registered"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenRequiredFieldsAreMissing() {
        user.setFirstName(""); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Required fields must not be null or empty"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenEmailFormatAreMissing() {
        user.setEmail("jhon.doe1gmail.com"); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Invalid email format"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenRangeSalary() {
        user.setSalary(new BigDecimal(-100)); // or set to null to test missing field

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Salary must be between 0 and 15,000,000"))
                .verify();
    }

    @Test
    void mustFailToSaveWhenRoleAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(roleRepository.existsByIdRole(1)).thenReturn(Mono.just(false));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.save(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("idRole does not exist"))
                .verify();
    }

}

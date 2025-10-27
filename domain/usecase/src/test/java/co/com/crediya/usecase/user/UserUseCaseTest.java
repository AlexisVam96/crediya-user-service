package co.com.crediya.usecase.user;

import co.com.crediya.model.exception.UserCustomException;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.security.Login;
import co.com.crediya.model.security.PasswordEncoder;
import co.com.crediya.model.security.TokenProvider;
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

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;


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
        user.setDocumentNumber("87654321");
        user.setPassword("securePassword");
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
        when(passwordEncoder.encode(user.getPassword())).thenReturn(Mono.just("encodedPassword"));

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

    @Test
    void mustLoginSuccessfully() {
        Login login = new Login();
        login.setEmail("john.doe@example.com");
        login.setPassword("plainPassword");

        User userDb = new User();
        userDb.setEmail("john.doe@example.com");
        userDb.setPassword("encodedPassword");
        userDb.setIdRole(1);

        Role roleDb = Role.builder().idRole(1).name("ADMIN")
                .description("description").build();

        // Mock repository and encoder
        when(userRepository.findByEmail(login.getEmail())).thenReturn(Mono.just(userDb));
        when(passwordEncoder.matches(login.getPassword(), userDb.getPassword())).thenReturn(Mono.just(true));
        when(roleRepository.findByIdRole(userDb.getIdRole())).thenReturn(Mono.just(roleDb));
        when(tokenProvider.generateToken(any(), any())).thenReturn(Mono.just("jwt-token"));

        StepVerifier.create(userUseCase.login(login))
                .expectNextMatches(authToken -> "jwt-token".equals(authToken.getToken()))
                .verifyComplete();
    }

    @Test
    void mustFailLoginWithInvalidPassword() {
        Login userInput = new Login();
        userInput.setEmail("john.doe@example.com");
        userInput.setPassword("wrongPassword");

        User userDb = new User();
        userDb.setEmail("john.doe@example.com");
        userDb.setPassword("encodedPassword");
        userDb.setIdRole(1);

        when(userRepository.findByEmail(userInput.getEmail())).thenReturn(Mono.just(userDb));
        when(passwordEncoder.matches(userInput.getPassword(), userDb.getPassword())).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.login(userInput))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("Invalid credentials"))
                .verify();
    }

    @Test
    void mustFindUserByDocumentNumberSuccessfully() {
        when(userRepository.findByDocumentNumber(user.getDocumentNumber())).thenReturn(Mono.just(user));
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.getUserByDocumentNumber(user.getDocumentNumber()))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustFailWhenUserByDocumentNumberNotFound() {
        when(userRepository.findByDocumentNumber("notfound")).thenReturn(Mono.empty());
        when(transactionManager.doInTransaction(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(userUseCase.getUserByDocumentNumber("notfound"))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserCustomException &&
                                throwable.getMessage().equals("User's document number not found"))
                .verify();
    }

}

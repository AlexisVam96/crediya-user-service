    package co.com.crediya.security;

    import co.com.crediya.model.exception.ErrorType;
    import co.com.crediya.model.exception.UserCustomException;
    import lombok.AllArgsConstructor;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.io.buffer.DataBuffer;
    import org.springframework.http.HttpMethod;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
    import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
    import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
    import org.springframework.security.config.web.server.ServerHttpSecurity;
    import org.springframework.security.web.server.SecurityWebFilterChain;
    import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
    import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
    import reactor.core.publisher.Mono;

    import java.nio.charset.StandardCharsets;

    @Configuration
    @EnableWebFluxSecurity
    public class SecurityConfig {

        private final JwtAuthenticationManager jwtAuthenticationManager;
        private final JwtSecurityContextRepository jwtSecurityContextRepository;

        public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager, JwtSecurityContextRepository jwtSecurityContextRepository) {
            this.jwtSecurityContextRepository = jwtSecurityContextRepository;
            this.jwtAuthenticationManager = jwtAuthenticationManager;
        }

        @Bean
        public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // desactiva Basic Auth
                    .formLogin(ServerHttpSecurity.FormLoginSpec::disable) //
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/api/v1/login/**").permitAll()
                            .pathMatchers(HttpMethod.POST, "/api/v1/user").hasRole("ADMIN")
                            .pathMatchers(
                                    "/swagger-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/webjars/**"
                            ).permitAll()// login/register público
                            .anyExchange().authenticated()               // lo demás requiere JWT
                    )
                    .authenticationManager(jwtAuthenticationManager)
                    .securityContextRepository(jwtSecurityContextRepository)
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) // 401
                            .accessDeniedHandler(customAccessDeniedHandler()) // 403
                    )
                    .build();
        }

        @Bean
        public ServerAccessDeniedHandler customAccessDeniedHandler() {
            return (exchange, denied) -> {
                return Mono.error(new UserCustomException("Access Denied: You do not have permission to access this resource.", ErrorType.FORBIDDEN));

            };
        }

    }

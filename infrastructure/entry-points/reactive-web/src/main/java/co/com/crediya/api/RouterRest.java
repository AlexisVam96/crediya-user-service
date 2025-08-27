package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/user",
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "listenGETAllUsers",
            operation = @Operation(
                operationId = "findAllUsers",
                summary = "Listar Usuarios",
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/user", produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.POST,
            beanClass = Handler.class,
            beanMethod = "listenPOSTSaveUser",
            operation = @Operation(
                operationId = "saveUser",
                summary = "Crear Usuario",
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Usuario creado correctamente",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class)
                        )
                    ),
                    @ApiResponse(responseCode = "400", description = "Error en la petición")
                },
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo Usuario",
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = CreateUserDto.class),
                        examples = {
                            @ExampleObject(
                                name = "Usuario básico",
                                description = "Ejemplo con datos mínimos",
                                value = "{\n" +
                                        "  \"firstName\": \"John\",\n" +
                                        "  \"lastName\": \"Doe\",\n" +
                                        "  \"birthDate\": \"1996-11-25\",\n" +
                                        "  \"address\": \"123 Main St\",\n" +
                                        "  \"phoneNumber\": \"+1234567890\",\n" +
                                        "  \"email\": \"john.doe@crediya.com\",\n" +
                                        "  \"salary\": 3500.50,\n" +
                                        "  \"idRole\": 2\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario administrador",
                                description = "Ejemplo con rol administrador",
                                value = "{\n" +
                                        "  \"firstName\": \"Maria\",\n" +
                                        "  \"lastName\": \"Lopez\",\n" +
                                        "  \"birthDate\": \"1990-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"maria.lopez@crediya.com\",\n" +
                                        "  \"salary\": 5000.00,\n" +
                                        "  \"idRole\": 1\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario con formato email inválido",
                                description = "Ejemplo con rol usuario y formato de email inválido",
                                value = "{\n" +
                                        "  \"firstName\": \"Juan\",\n" +
                                        "  \"lastName\": \"Perez\",\n" +
                                        "  \"birthDate\": \"1999-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"juan.perezcrediya.com\",\n" +
                                        "  \"salary\": 7000.00,\n" +
                                        "  \"idRole\": 2\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario sin campos requeridos",
                                description = "Ejemplo con rol usuario y sin campos requeridos",
                                value = "{\n" +
                                        "  \"firstName\": \"Carlos\",\n" +
                                        "  \"birthDate\": \"1999-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"carlos.perez@crediya.com\",\n" +
                                        "  \"salary\": 7000.00,\n" +
                                        "  \"idRole\": 2\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario con salario fuera del rango establecido",
                                description = "Ejemplo con rol usuario y salario negativo",
                                value = "{\n" +
                                        "  \"firstName\": \"Ana\",\n" +
                                        "  \"lastName\": \"Sanchez\",\n" +
                                        "  \"birthDate\": \"1999-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"ana.sanchez@crediya.com\",\n" +
                                        "  \"salary\": -1000.0,\n" +
                                        "  \"idRole\": 2\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario con email existente",
                                description = "Ejemplo con rol usuario y email ya registrado",
                                value = "{\n" +
                                        "  \"firstName\": \"Lucas\",\n" +
                                        "  \"lastName\": \"Sandoval\",\n" +
                                        "  \"birthDate\": \"1999-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"lucas.sandoval@crediya.com\",\n" +
                                        "  \"salary\": 1000.0,\n" +
                                        "  \"idRole\": 2\n" +
                                        "}"
                            ),
                            @ExampleObject(
                                name = "Usuario con role inválido",
                                description = "Ejemplo con rol inválido",
                                value = "{\n" +
                                        "  \"firstName\": \"Ramiro\",\n" +
                                        "  \"lastName\": \"Jimenez\",\n" +
                                        "  \"birthDate\": \"1999-05-10\",\n" +
                                        "  \"address\": \"456 Elm St\",\n" +
                                        "  \"phoneNumber\": \"+9876543210\",\n" +
                                        "  \"email\": \"ramiro.jimenez@crediya.com\",\n" +
                                        "  \"salary\": 1000.0,\n" +
                                        "  \"idRole\": 3\n" +
                                        "}"
                            )
                        }
                    )
                )
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/user"), handler::listenGETAllUsers)
                .andRoute(POST("/api/v1/user"), handler::listenPOSTSaveUser);
    }
}

package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(description = "User's id user", example = "1")
    private Integer idUser;

    @Schema(description = "User's first name", example = "Jhon")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's birth date", example = "1996-11-25", type = "string", format = "date")
    private LocalDate birthDate;

    @Schema(description = "User's address", example = "123 Main St")
    private String address;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User's email address", example = "Jhon.doe@crediya.com")
    private String email;

    @Schema(description = "User's salary", example = "3500.50")
    private BigDecimal salary;

    @Schema(description = "User's role ID", example = "2")
    private Integer idRole;

    @Schema(description = "User's document number", example = "123456789")
    private String documentNumber;

    private String password;
}

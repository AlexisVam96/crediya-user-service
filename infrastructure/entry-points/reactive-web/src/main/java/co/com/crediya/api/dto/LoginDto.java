package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {

    @Schema(description = "User's email", example = "jhon.doe@crediya.com")
    private String email;

    @Schema(description = "User's password", example = "*****")
    private String password;
}

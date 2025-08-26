package co.com.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {

    private Integer idUser;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private String salary;
    private Integer idRole;
}

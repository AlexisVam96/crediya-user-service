package co.com.crediya.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDto {

    private Integer id_user;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address;
    private String phoneNumber;
    private String email;
    private String salary;
}

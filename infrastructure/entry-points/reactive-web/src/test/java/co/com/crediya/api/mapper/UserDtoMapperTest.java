package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.UserDto;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private final UserDtoMapper mapper = Mappers.getMapper(UserDtoMapper.class);

    @Test
    void toModel_shouldMapCreateUserDtoToUser() {
        CreateUserDto dto = new CreateUserDto();
        dto.setIdUser(1);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setAddress("123 Main St");
        dto.setBirthDate(LocalDate.of(19996,11,25));
        dto.setSalary(new BigDecimal(10000));
        dto.setPhoneNumber("1234567890");
        dto.setIdRole(1);

        User user = mapper.toModel(dto);

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void toResponse_shouldMapUserToUserDto() {
        User user = new User();
        user.setIdUser(1);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setEmail("jane.smith@example.com");
        user.setAddress("123 Main St");
        user.setBirthDate(LocalDate.of(19996,11,25));
        user.setSalary(new BigDecimal(10000));
        user.setPhoneNumber("1234567890");
        user.setIdRole(1);

        UserDto dto = mapper.toResponse(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Smith");
        assertThat(dto.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void toResponse_shouldMapUserListToUserDtoList() {
        User user = new User();
        user.setIdUser(1);
        user.setFirstName("Alice");
        user.setLastName("Brown");
        user.setEmail("alice.brown@example.com");
        user.setAddress("123 Main St");
        user.setBirthDate(LocalDate.of(19996,11,25));
        user.setSalary(new BigDecimal(10000));
        user.setPhoneNumber("1234567890");
        user.setIdRole(1);;

        List<UserDto> dtos = mapper.toResponse(Collections.singletonList(user));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getEmail()).isEqualTo("alice.brown@example.com");
    }
}
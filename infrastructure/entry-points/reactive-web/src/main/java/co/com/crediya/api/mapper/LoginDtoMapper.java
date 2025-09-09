package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoginDto;
import co.com.crediya.model.security.Login;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginDtoMapper {

    Login toModel(LoginDto user);
}

package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.TokenDto;
import co.com.crediya.model.security.AuthToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenDtoMapper {

    TokenDto toResponse(AuthToken user);
}

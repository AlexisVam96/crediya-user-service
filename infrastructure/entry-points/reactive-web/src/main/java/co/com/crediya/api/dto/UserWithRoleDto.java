package co.com.crediya.api.dto;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;

import java.util.List;

public record UserWithRoleDto(User user, List<Role> roles) {
}
